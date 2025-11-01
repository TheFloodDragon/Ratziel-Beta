package cn.fd.ratziel.module.script.api

import javax.script.ScriptException

/**
 * ScriptExecutor - 脚本执行器
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:26
 */
interface ScriptExecutor {

    /**
     * 创建一个新的脚本编译器
     */
    fun compiler(): ScriptCompiler

    /**
     * 创建一个新的脚本评估器
     */
    fun evaluator(): ScriptEvaluator

    /**
     * 构建脚本
     *
     * @param  source 脚本源
     * @param environment 脚本环境
     */
    fun build(source: ScriptSource, environment: ScriptEnvironment = ScriptEnvironment()): Result<ScriptContent>

    /**
     * 评估脚本
     *
     * @param script      原始脚本
     * @param environment 脚本运行环境
     * @throws ScriptException 当脚本评估中产生错误时抛出
     */
    fun eval(script: ScriptContent, environment: ScriptEnvironment = ScriptEnvironment()): Result<Any?>

}
