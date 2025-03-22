package cn.fd.ratziel.module.script.api;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptContent
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:28
 */
public interface ScriptContent {

    /**
     * 获取脚本原始内容
     */
    @NotNull
    String getContent();

    /**
     * 获取脚本执行器
     */
    @NotNull
    ScriptExecutor getExecutor();

}
