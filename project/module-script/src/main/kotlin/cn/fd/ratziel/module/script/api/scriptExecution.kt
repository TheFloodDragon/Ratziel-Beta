package cn.fd.ratziel.module.script.api

/**
 * ScriptCompiler - 脚本编译器
 *
 * @author TheFloodDragon
 * @since 2025/11/1 19:55
 */
interface ScriptCompiler {

    /**
     * 编译脚本
     *
     * @param  source 脚本源
     * @param environment 脚本环境
     */
    fun compile(source: ScriptSource, environment: ScriptEnvironment): CompiledScript

}

/**
 * ScriptEvaluator - 脚本评估器
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
    fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any?

}


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
    @Throws(ScriptCompilationException::class)
    fun build(source: ScriptSource, environment: ScriptEnvironment = ScriptEnvironment()): ScriptContent

    /**
     * 评估脚本
     *
     * @param script      原始脚本
     * @param environment 脚本运行环境
     */
    @Throws(ScriptEvaluationException::class)
    fun eval(script: ScriptContent, environment: ScriptEnvironment = ScriptEnvironment()): Any?

}

/**
 * IntegratedScriptExecutor - 集成的脚本执行器
 *
 * @author TheFloodDragon
 * @since 2025/11/1 20:18
 */
abstract class IntegratedScriptExecutor : ScriptExecutor, ScriptCompiler, ScriptEvaluator {

    final override fun build(source: ScriptSource, environment: ScriptEnvironment): ScriptContent {
        try {
            return this.compile(source, environment)
        } catch (ex: ScriptCompilationException) {
            throw ex
        } catch (cause: Throwable) {
            throw ScriptCompilationException(cause, source, this)
        }
    }

    final override fun eval(script: ScriptContent, environment: ScriptEnvironment): Any? {
        try {
            return if (script is CompiledScript) {
                script.eval(environment)
            } else {
                this.evaluate(script, environment)
            }
        } catch (ex: ScriptEvaluationException) {
            throw ex
        } catch (cause: Throwable) {
            throw ScriptEvaluationException(cause, script, this)
        }
    }

}
