package cn.fd.ratziel.script.impl;

import cn.fd.ratziel.script.api.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * SimpleScript
 *
 * @author TheFloodDragon
 * @since 2024/7/15 13:25
 */
public class SimpleScript implements EvaluableScript, StorableScript, ScriptContent {

    public SimpleScript(@NotNull String content, @NotNull ScriptExecutor executor) {
        this.content = content;
        this.executor = executor;
    }

    private final String content;
    private final ScriptExecutor executor;
    private CompiledScript compiledScript = null;

    @Override
    public @Nullable Object evaluate(@NotNull ScriptEnvironment environment) throws ScriptException {
        // 已存在编译过的脚本, 或者脚本可被编译(已通过compile方法编译)
        if (compiledScript != null || compile(getExecutor())) {
            // 直接用编译后的脚本评估
            return compiledScript.eval(environment.getBindings());
        } else {
            // 通过执行器评估脚本
            return getExecutor().evaluate(this, environment);
        }
    }

    @Override
    public boolean compile(@NotNull ScriptExecutor executor) throws ScriptException {
        if (executor instanceof Compilable) {
            // 编译脚本
            this.compiledScript = ((Compilable) executor).compile(getContent());
            return true;
        } else return false;
    }

    @Override
    public @Nullable CompiledScript getCompiled() {
        return compiledScript;
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
