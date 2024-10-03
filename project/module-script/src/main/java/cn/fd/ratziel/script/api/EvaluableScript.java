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
@Deprecated
public interface EvaluableScript {

    /**
     * 评估脚本
     *
     * @param environment 覆写的的脚本环境
     * @throws ScriptException 当脚本评估中产生错误时抛出
     */
    @Nullable
    Object evaluate(@NotNull ScriptEnvironment environment) throws ScriptException;

}
