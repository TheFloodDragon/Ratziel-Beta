package cn.fd.ratziel.script.api;

import org.jetbrains.annotations.NotNull;

/**
 * ScriptType - 脚本类型
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:23
 */
public interface ScriptType {

    /**
     * 获取脚本的所有名称
     */
    @NotNull
    String[] getNames();

    /**
     * 获取脚本执行器
     */
    @NotNull
    ScriptExecutor getExecutor();

}
