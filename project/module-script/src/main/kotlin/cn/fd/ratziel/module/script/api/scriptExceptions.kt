package cn.fd.ratziel.module.script.api

/**
 * ScriptException
 *
 * @author TheFloodDragon
 * @since 2025/11/1 19:58
 */
sealed class ScriptException(cause: Throwable) : Exception(cause.message,cause)

/**
 * ScriptCompilationException
 *
 * @author TheFloodDragon
 * @since 2025/11/1 19:59
 */
class ScriptCompilationException(cause: Throwable,val source: ScriptSource, val compiler: ScriptCompiler) : ScriptException(cause)

/**
 * ScriptEvaluationException
 *
 * @author TheFloodDragon
 * @since 2025/11/1 19:59
 */
class ScriptEvaluationException(cause: Throwable,val script: ScriptContent, val evaluator: ScriptEvaluator) : ScriptException(cause)
