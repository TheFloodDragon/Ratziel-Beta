package cn.fd.ratziel.module.script.benchmark

import cn.fd.ratziel.module.script.benchmark.engine.FluxonBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.GraalJsBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.JexlBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.KotlinScriptingBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.NashornBenchmarkCase

/** 一份已装配好的脚本源码 + 绑定工厂。`content` 已经过 [BenchmarkSettings.apply] 占位符替换。 */
internal data class ScriptSample(
    val path: String,
    val content: String,
    val bindingsFactory: () -> MutableMap<String, Any?>,
)

/** 基准用例声明：[id] 用作样本文件名的查找键；显示名编码在 [ScriptBenchmarkBase] 的 `@Param` 字符串里。 */
internal data class BenchmarkScriptCase(
    val id: String,
    val bindingsFactory: () -> MutableMap<String, Any?> = { linkedMapOf() },
)

internal interface BenchmarkCase<P : Any> {

    val engineName: String

    val samples: Map<String, ScriptSample>

    fun sample(scriptCase: BenchmarkScriptCase): ScriptSample = samples.getValue(scriptCase.id)

    fun prepare(sample: ScriptSample): P

    fun execute(prepared: P): Any?

    fun evaluate(sample: ScriptSample): Any? {
        val prepared = prepare(sample)
        try {
            return execute(prepared)
        } finally {
            dispose(prepared)
        }
    }

    fun dispose(prepared: P) = Unit
}

/**
 * 所有可用的脚本引擎适配器。这是 engineName 与 [BenchmarkCase] 的**唯一对应表**，
 * JMH 通过 `BENCHMARK_CASES.first { it.engineName == engine }` 查找，无需额外字符串常量或解析函数。
 */
internal val BENCHMARK_CASES: List<BenchmarkCase<*>> = listOf(
    FluxonBenchmarkCase,
    GraalJsBenchmarkCase,
    NashornBenchmarkCase,
    JexlBenchmarkCase,
    KotlinScriptingBenchmarkCase,
)

/** 读取 `/samples/$sampleDirectory/$id$sampleExtension` 下所有基准用例的脚本文件。 */
internal fun engineSamples(sampleDirectory: String, sampleExtension: String): Map<String, ScriptSample> {
    return BENCHMARK_SCRIPT_CASES.mapNotNull { scriptCase ->
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
    return ScriptSample(
        path = path,
        content = BENCHMARK_SETTINGS.apply(content),
        bindingsFactory = bindingsFactory,
    )
}
