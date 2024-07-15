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

    public SimpleScript(@NotNull String content) {
        this.content = content;
    }

    private final String content;
    private CompiledScript compiledScript = null;

    @Override
    public @Nullable Object evaluate(@NotNull ScriptExecutor executor, @NotNull ScriptEnvironment environment) throws ScriptException {
        // 若脚本没经过编译, 并且该脚本执行器可以编译此脚本
        if (compiledScript == null && executor instanceof Compilable) {
            // 编译脚本
            CompiledScript compiled = ((Compilable) executor).compile(getContent());
            // 存储编译后的脚本
            setCompiled(compiled);
            // 直接评估脚本
            return compiled.eval(environment.getBindings());
        } else {
            // 通过执行器评估脚本
            return executor.evaluate(this, environment);
        }
    }

    @Override
    public @Nullable CompiledScript getCompiled() {
        return compiledScript;
    }

    @Override
    public void setCompiled(@NotNull CompiledScript compiled) {
        this.compiledScript = compiled;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return getContent();
    }

}
