package cn.fd.ratziel.script.api;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptException;

/**
 * EvaluableScript
 *
 * @author TheFloodDragon
 * @since 2024/7/15 13:14
 */
public interface EvaluableScript {

    /**
     * 评估脚本
     *
     * @param environment 覆写的的脚本环境
     * @throws ScriptException 当脚本评估中产生错误时抛出
     */
    @Nullable
    Object evaluate(@NotNull ScriptEnvironment environment) throws ScriptException;

    /**
     * 获取脚本执行器
     *
     * @return 覆写的脚本执行器
     */
    @NotNull
    ScriptExecutor getExecutor();

}
