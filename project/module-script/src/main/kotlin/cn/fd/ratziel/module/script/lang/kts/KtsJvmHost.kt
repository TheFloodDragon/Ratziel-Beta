package cn.fd.ratziel.module.script.lang.kts

import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

/**
 * KtsJvmHost
 *
 * @author TheFloodDragon
 * @since 2025/10/2 18:20
 */
open class KtsJvmHost(baseHostConfiguration: ScriptingHostConfiguration? = null) : BasicJvmScriptingHost(baseHostConfiguration) {

    /**
     * Compile [script].
     */
    open fun compile(
        script: SourceCode,
        compilationConfiguration: ScriptCompilationConfiguration,
    ): ResultWithDiagnostics<CompiledScript> =
        runInCoroutineContext {
            compiler(script, compilationConfiguration)
        }

    /**
     * Evaluate provided compiled script.
     */
    open fun eval(
        compiled: CompiledScript,
        evaluationConfiguration: ScriptEvaluationConfiguration,
    ): ResultWithDiagnostics<EvaluationResult> =
        runInCoroutineContext {
            evaluator(compiled, evaluationConfiguration)
        }

}