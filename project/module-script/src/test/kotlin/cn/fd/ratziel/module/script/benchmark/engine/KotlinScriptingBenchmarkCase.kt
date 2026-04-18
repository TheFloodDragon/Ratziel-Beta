package cn.fd.ratziel.module.script.benchmark.engine

import cn.fd.ratziel.module.script.benchmark.BenchmarkCase
import cn.fd.ratziel.module.script.benchmark.ScriptSample
import cn.fd.ratziel.module.script.benchmark.engineSamples
import kotlin.reflect.KClass
import kotlin.script.experimental.api.CompiledScript as KotlinCompiledScript
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.KotlinType
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

internal object KotlinScriptingBenchmarkCase : BenchmarkCase<KotlinPreparedScript> {

    override val engineName: String = "KotlinScripting"

    override val samples: Map<String, ScriptSample> = engineSamples("kotlin", ".kts")

    override fun prepare(sample: ScriptSample): KotlinPreparedScript {
        val bindings = sample.bindingsFactory()
        val compiled = BenchmarkKtsHost.compile(
            sample.content.toScriptSource(sample.path.substringAfterLast('/')),
            benchmarkCompilationConfiguration(bindings),
        ).valueOrThrow()
        return KotlinPreparedScript(compiled, benchmarkEvaluationConfiguration(bindings))
    }

    override fun execute(prepared: KotlinPreparedScript): Any? {
        return BenchmarkKtsHost.eval(prepared.script, prepared.evaluationConfiguration)
            .valueOrThrow()
            .returnValue
            .unwrap()
    }
}

internal data class KotlinPreparedScript(
    val script: KotlinCompiledScript,
    val evaluationConfiguration: ScriptEvaluationConfiguration,
)

private object BenchmarkKtsHost : BasicJvmScriptingHost() {

    fun compile(
        script: kotlin.script.experimental.api.SourceCode,
        compilationConfiguration: ScriptCompilationConfiguration,
    ): ResultWithDiagnostics<KotlinCompiledScript> = runInCoroutineContext {
        compiler(script, compilationConfiguration)
    }

    fun eval(
        compiled: KotlinCompiledScript,
        evaluationConfiguration: ScriptEvaluationConfiguration,
    ): ResultWithDiagnostics<EvaluationResult> = runInCoroutineContext {
        evaluator(compiled, evaluationConfiguration)
    }
}

private fun benchmarkCompilationConfiguration(bindings: Map<String, Any?>): ScriptCompilationConfiguration {
    return ScriptCompilationConfiguration {
        jvm {
            dependenciesFromCurrentContext(wholeClasspath = true)
        }
        if (bindings.isNotEmpty()) {
            providedProperties(bindings.mapValues { (_, value) -> value.toKotlinType() })
        }
    }
}

private fun benchmarkEvaluationConfiguration(bindings: Map<String, Any?>): ScriptEvaluationConfiguration {
    return ScriptEvaluationConfiguration {
        if (bindings.isNotEmpty()) {
            providedProperties(bindings)
        }
    }
}

private fun Any?.toKotlinType(): KotlinType {
    val kClass = (this?.let { it::class } ?: Any::class) as KClass<*>
    return KotlinType(kClass)
}

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
