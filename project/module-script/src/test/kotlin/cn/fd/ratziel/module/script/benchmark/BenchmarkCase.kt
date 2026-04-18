package cn.fd.ratziel.module.script.benchmark

internal data class ScriptSample(
    val path: String,
    val content: String,
    val bindingsFactory: () -> MutableMap<String, Any?> = { linkedMapOf() },
)

internal data class BenchmarkScriptCase(
    val id: String,
    val displayName: String,
    val bindingsFactory: () -> MutableMap<String, Any?> = { linkedMapOf() },
)

internal interface BenchmarkCase<P : Any> {

    val engineName: String

    val samples: Map<String, ScriptSample>

    fun supports(scriptCase: BenchmarkScriptCase): Boolean = samples.containsKey(scriptCase.id)

    fun sample(scriptCase: BenchmarkScriptCase): ScriptSample = samples.getValue(scriptCase.id)

    fun prepare(sample: ScriptSample): P

    fun execute(prepared: P): Any?

    fun dispose(prepared: P) = Unit

}

internal fun engineSamples(
    sampleDirectory: String,
    sampleExtension: String,
    scriptCases: List<BenchmarkScriptCase> = BENCHMARK_SCRIPT_CASES,
): Map<String, ScriptSample> {
    return scriptCases.mapNotNull { scriptCase ->
        loadSampleOrNull(
            path = "/samples/$sampleDirectory/${scriptCase.id}$sampleExtension",
            bindingsFactory = scriptCase.bindingsFactory,
        )?.let { scriptCase.id to it }
    }.toMap()
}

private fun loadSampleOrNull(
    path: String,
    bindingsFactory: () -> MutableMap<String, Any?>,
): ScriptSample? {
    val content = BenchmarkCase::class.java.getResource(path)?.readText() ?: return null
    return ScriptSample(path = path, content = content, bindingsFactory = bindingsFactory)
}
