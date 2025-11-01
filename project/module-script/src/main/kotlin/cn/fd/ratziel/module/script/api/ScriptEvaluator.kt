package cn.fd.ratziel.module.script.api

/**
 * ScriptEvaluator
 *
 * @author TheFloodDragon
 * @since 2025/11/1 19:57
 */
interface ScriptEvaluator {

    /**
     * 评估脚本
     *
     * @param script      原始脚本
     * @param environment 脚本运行环境
     */
    @Throws(ScriptEvaluationException::class)
    fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any?

}