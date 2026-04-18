package cn.fd.ratziel.module.script.performance

internal data class ScriptSample(
    val path: String,
    val content: String,
)

internal data class PerformanceScriptCase(
    val id: String,
    val displayName: String,
    val fileNames: Map<String, String>,
)

internal interface BenchmarkCase<P : Any> {

    val engineName: String

    val samples: Map<String, ScriptSample>

    fun supports(scriptCase: PerformanceScriptCase): Boolean = samples.containsKey(scriptCase.id)

    fun sample(scriptCase: PerformanceScriptCase): ScriptSample = samples.getValue(scriptCase.id)

    fun prepare(sample: ScriptSample): P

    fun execute(prepared: P): Any?

    fun dispose(prepared: P) = Unit

}

internal fun engineSamples(
    sampleDirectory: String,
    scriptCases: List<PerformanceScriptCase> = PERFORMANCE_SCRIPT_CASES,
): Map<String, ScriptSample> {
    return scriptCases.mapNotNull { scriptCase ->
        val fileName = scriptCase.fileNames[sampleDirectory] ?: return@mapNotNull null
        loadSampleOrNull("/performance-samples/$sampleDirectory/$fileName")
            ?.let { scriptCase.id to it }
    }.toMap()
}

private fun loadSampleOrNull(path: String): ScriptSample? {
    val content = ScriptEnginePerformanceTest::class.java.getResource(path)?.readText() ?: return null
    return ScriptSample(path = path, content = content)
}
