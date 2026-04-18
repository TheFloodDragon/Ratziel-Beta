package cn.fd.ratziel.module.script.performance

import cn.fd.ratziel.module.script.lang.kts.KtsJvmHost
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

internal object KotlinScriptingBenchmarkCase : BenchmarkCase<KotlinPreparedScript> {

    private val host = KtsJvmHost()

    override val engineName: String = "KotlinScripting"

    override val samples: Map<String, ScriptSample> = engineSamples("kotlin")

    override fun prepare(sample: ScriptSample): KotlinPreparedScript {
        val compiled = host.compile(
            sample.content.toScriptSource(sample.path.substringAfterLast('/')),
            KotlinPerformanceCompilationConfiguration,
        ).valueOrThrow()
        return KotlinPreparedScript(host, compiled, KotlinPerformanceEvaluationConfiguration)
    }

    override fun execute(prepared: KotlinPreparedScript): Any? {
        return prepared.host.eval(prepared.script, prepared.evaluationConfiguration)
            .valueOrThrow()
            .returnValue
            .unwrap()
    }

}

internal data class KotlinPreparedScript(
    val host: KtsJvmHost,
    val script: CompiledScript,
    val evaluationConfiguration: ScriptEvaluationConfiguration,
)

internal object KotlinPerformanceCompilationConfiguration : ScriptCompilationConfiguration({
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
})

internal object KotlinPerformanceEvaluationConfiguration : ScriptEvaluationConfiguration({
})

private fun ResultValue.unwrap(): Any? {
    return when (this) {
        is ResultValue.Value -> value
        is ResultValue.Unit -> Unit
        is ResultValue.Error -> error("Kotlin 脚本执行失败: $error")
        is ResultValue.NotEvaluated -> error("Kotlin 脚本未执行")
    }
}

private fun <T> ResultWithDiagnostics<T>.valueOrThrow(): T {
    return when (this) {
        is ResultWithDiagnostics.Success -> value
        is ResultWithDiagnostics.Failure -> error(renderDiagnostics(reports))
    }
}

private fun renderDiagnostics(reports: List<ScriptDiagnostic>): String {
    return reports.joinToString(separator = "\n") { report ->
        buildString {
            append(report.severity)
            append(": ")
            append(report.message)
            report.exception?.let {
                append(" (")
                append(it::class.qualifiedName)
                append(": ")
                append(it.message)
                append(')')
            }
        }
    }
}
