package cn.fd.ratziel.module.script.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptException;

/**
 * ScriptExecutor - 脚本执行者
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:26
 */
public interface ScriptExecutor {

    /**
     * 构建脚本
     *
     * @param script 脚本文本
     */
    @NotNull
    ScriptContent build(@NotNull String script);

    /**
     * 评估脚本
     *
     * @param script      原始脚本
     * @param environment 脚本运行环境
     * @throws ScriptException 当脚本评估中产生错误时抛出
     */
    @Nullable
    Object evaluate(@NotNull ScriptContent script, @NotNull ScriptEnvironment environment) throws ScriptException;

    /**
     * 评估脚本
     *
     * @param script      原始脚本
     * @param environment 脚本运行环境
     * @throws ScriptException 当脚本评估中产生错误时抛出
     */
    @Nullable
    default Object evaluate(@NotNull String script, @NotNull ScriptEnvironment environment) throws ScriptException {
        return evaluate(new LiteralScriptContent(script, this), environment);
    }

}
