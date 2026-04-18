package cn.fd.ratziel.module.script.performance

internal enum class ScriptDialect(val directory: String) {
    JavaScript("javascript"),
    Jexl("jexl"),
    Kotlin("kotlin"),
    Fluxon("fluxon"),
}

internal data class ScriptSample(
    val path: String,
    val content: String,
)

internal data class PerformanceScriptCase(
    val id: String,
    val displayName: String,
    val fileNames: Map<ScriptDialect, String>,
) {

    fun fileName(dialect: ScriptDialect): String = fileNames.getValue(dialect)

}

internal interface BenchmarkCase<P : Any> {

    val engineName: String

    val dialect: ScriptDialect

    val samples: Map<String, ScriptSample>

    fun supports(scriptCase: PerformanceScriptCase): Boolean = samples.containsKey(scriptCase.id)

    fun sample(scriptCase: PerformanceScriptCase): ScriptSample = samples.getValue(scriptCase.id)

    fun prepare(sample: ScriptSample): P

    fun execute(prepared: P): Any?

    fun dispose(prepared: P) = Unit

}

internal fun engineSamples(
    dialect: ScriptDialect,
    scriptCases: List<PerformanceScriptCase> = PERFORMANCE_SCRIPT_CASES,
): Map<String, ScriptSample> {
    return scriptCases.associate { scriptCase ->
        scriptCase.id to loadSample("/performance-samples/${dialect.directory}/${scriptCase.fileName(dialect)}")
    }
}

private fun loadSample(path: String): ScriptSample {
    val content = ScriptEnginePerformanceTest::class.java.getResource(path)?.readText()
        ?: error("无法读取脚本样本: $path")
    return ScriptSample(path = path, content = content)
}
