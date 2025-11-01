package cn.fd.ratziel.module.script.api

/**
 * ScriptException
 *
 * @author TheFloodDragon
 * @since 2025/11/1 19:58
 */
sealed class ScriptException(cause: Throwable) : Exception(cause)

/**
 * ScriptCompilationException
 *
 * @author TheFloodDragon
 * @since 2025/11/1 19:59
 */
class ScriptCompilationException(val source: ScriptSource, cause: Throwable, val compiler: ScriptCompiler) : ScriptException(cause)

/**
 * ScriptEvaluationException
 *
 * @author TheFloodDragon
 * @since 2025/11/1 19:59
 */
class ScriptEvaluationException(val script: ScriptContent, cause: Throwable, val evaluator: ScriptEvaluator? = null) : ScriptException(cause)
