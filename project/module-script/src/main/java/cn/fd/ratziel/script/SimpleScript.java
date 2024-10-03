package cn.fd.ratziel.script;

import cn.fd.ratziel.script.api.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.util.concurrent.CompletableFuture;

/**
 * SimpleScript
 *
 * @author TheFloodDragon
 * @since 2024/7/15 13:25
 */
public class SimpleScript implements EvaluableScript, CacheableScript, ScriptContent {

    public SimpleScript(@NotNull String content, @NotNull ScriptExecutor executor) {
        this.content = content;
        this.executor = executor;
    }

    private final String content;
    private final ScriptExecutor executor;
    private CompletableFuture<CompiledScript> future = null;

    @Override
    public @Nullable Object evaluate(@NotNull ScriptEnvironment environment) throws ScriptException {
        CompiledScript compiled = getCompiled();
        // 已存在编译过的脚本
        if (compiled != null) {
            // 直接用编译后的脚本评估
            return compiled.eval(environment.getBindings());
        } else {
            // 尝试编译脚本, 如果没有编译过的话 (compile是异步执行的, 不然也不敢放在这)
            if (future == null) compile(getExecutor());
            // 通过执行器评估脚本
            return getExecutor().evaluate(this, environment);
        }
    }

    @Override
    public boolean compile(@NotNull ScriptExecutor executor) {
        if (executor instanceof Compilable) {
            // 编译脚本
            future = CompletableFuture.supplyAsync(() -> {
                try {
                    return ((Compilable) executor).compile(getContent());
                } catch (Exception e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
                return null;
            });
            return true;
        } else return false;
    }

    @Override
    public @Nullable CompiledScript getCompiled() {
        if (future != null && future.isDone())
            return future.getNow(null);
        else return null;
    }

    @Override
    public @NotNull ScriptExecutor getExecutor() {
        return executor;
    }

    @Override
    public @NotNull String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return getContent();
    }

}
