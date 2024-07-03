package cn.fd.ratziel.script;

import org.jetbrains.annotations.NotNull;

import javax.script.ScriptException;

/**
 * ScriptLanguage
 *
 * @author TheFloodDragon
 * @since 2024/6/30 08:16
 */
public interface ScriptLanguage {

    /**
     * 获取脚本的所有名称
     */
    @NotNull
    String[] getNames();

    /**
     * 评估脚本
     *
     * @param script      原始脚本
     * @param environment 脚本环境
     * @throws ScriptException 当脚本评估中产生错误时抛出
     */
    Object evaluate(@NotNull ScriptStorage script, @NotNull ScriptEnvironment environment) throws ScriptException;

}