package cn.fd.ratziel.module.script.performance

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.Comparator
import java.util.Locale
import kotlin.system.measureNanoTime
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit

@Tag("performance")
class ScriptEnginePerformanceTest {

    @Test
    fun `benchmarks script compile latency`() {
        val settings = BenchmarkSettings.fromSystemProperties()
        val results = PERFORMANCE_SCRIPT_CASES.flatMap { scriptCase ->
            BENCHMARK_CASES.filter { it.supports(scriptCase) }.map { benchmarkCompile(it, scriptCase, settings) }
        }

        println(renderCompileReport(results, settings))
    }

    @Test
    fun `benchmarks script hot execution latency`() {
        val settings = BenchmarkSettings.fromSystemProperties()
        val results = PERFORMANCE_SCRIPT_CASES.flatMap { scriptCase ->
            BENCHMARK_CASES.filter { it.supports(scriptCase) }.map { benchmarkHot(it, scriptCase, settings) }
        }

        println(renderHotReport(results, settings))
    }

    @Test
    fun `benchmarks script cold startup latency`() {
        val settings = BenchmarkSettings.fromSystemProperties()
        val results = PERFORMANCE_SCRIPT_CASES.flatMap { scriptCase ->
            BENCHMARK_CASES.filter { it.supports(scriptCase) }.map { benchmarkCold(it, scriptCase, settings) }
        }

        println(renderColdReport(results, settings))
    }

}

private val BENCHMARK_CASES = listOf(
    GraalJsBenchmarkCase,
    NashornBenchmarkCase,
    JexlBenchmarkCase,
    KotlinScriptingBenchmarkCase,
    FluxonBenchmarkCase,
)

private data class BenchmarkSettings(
    val buildWarmupIterations: Int,
    val buildMeasuredIterations: Int,
    val hotWarmupIterations: Int,
    val hotMeasuredIterations: Int,
    val hotInvocationCount: Int,
    val coldWarmupIterations: Int,
    val coldMeasuredIterations: Int,
) {

    init {
        require(buildWarmupIterations >= 0) { "buildWarmupIterations 不能小于 0" }
        require(buildMeasuredIterations > 0) { "buildMeasuredIterations 必须大于 0" }
        require(hotWarmupIterations >= 0) { "hotWarmupIterations 不能小于 0" }
        require(hotMeasuredIterations > 0) { "hotMeasuredIterations 必须大于 0" }
        require(hotInvocationCount > 0) { "hotInvocationCount 必须大于 0" }
        require(coldWarmupIterations >= 0) { "coldWarmupIterations 不能小于 0" }
        require(coldMeasuredIterations > 0) { "coldMeasuredIterations 必须大于 0" }
    }

    companion object {
        fun fromSystemProperties() = BenchmarkSettings(
            buildWarmupIterations = System.getProperty("ratziel.performance.build.warmup")?.toIntOrNull() ?: 1,
            buildMeasuredIterations = System.getProperty("ratziel.performance.build.iterations")?.toIntOrNull() ?: 2,
            hotWarmupIterations = System.getProperty("ratziel.performance.eval.warmup")?.toIntOrNull() ?: 1,
            hotMeasuredIterations = System.getProperty("ratziel.performance.eval.iterations")?.toIntOrNull() ?: 3,
            hotInvocationCount = System.getProperty("ratziel.performance.eval.invocations")?.toIntOrNull() ?: 200,
            coldWarmupIterations = System.getProperty("ratziel.performance.cold.warmup")?.toIntOrNull() ?: 1,
            coldMeasuredIterations = System.getProperty("ratziel.performance.cold.iterations")?.toIntOrNull() ?: 2,
        )
    }

}

private data class CompileBenchmarkResult(
    val engineName: String,
    val scriptCaseName: String,
    val samplePath: String,
    val samplesNs: List<Long>,
) {

    val averageMs: Double get() = samplesNs.average().nanoseconds.toDouble(DurationUnit.MILLISECONDS)
    val bestMs: Double get() = (samplesNs.minOrNull() ?: 0L).nanoseconds.toDouble(DurationUnit.MILLISECONDS)
    val worstMs: Double get() = (samplesNs.maxOrNull() ?: 0L).nanoseconds.toDouble(DurationUnit.MILLISECONDS)

}

private data class HotBenchmarkResult(
    val engineName: String,
    val scriptCaseName: String,
    val samplePath: String,
    val samplesNs: List<Long>,
    val invocationCount: Int,
) {

    val averageUsPerOp: Double get() = samplesNs.average().nanoseconds.toDouble(DurationUnit.MICROSECONDS) / invocationCount
    val bestUsPerOp: Double get() = (samplesNs.minOrNull() ?: 0L).nanoseconds.toDouble(DurationUnit.MICROSECONDS) / invocationCount
    val worstUsPerOp: Double get() = (samplesNs.maxOrNull() ?: 0L).nanoseconds.toDouble(DurationUnit.MICROSECONDS) / invocationCount
    val throughputOpsPerSec: Double get() = if (averageUsPerOp == 0.0) Double.POSITIVE_INFINITY else 1_000_000.0 / averageUsPerOp

}

private data class ColdBenchmarkResult(
    val engineName: String,
    val scriptCaseName: String,
    val samplePath: String,
    val samplesNs: List<Long>,
) {

    val averageMs: Double get() = samplesNs.average().nanoseconds.toDouble(DurationUnit.MILLISECONDS)
    val bestMs: Double get() = (samplesNs.minOrNull() ?: 0L).nanoseconds.toDouble(DurationUnit.MILLISECONDS)
    val worstMs: Double get() = (samplesNs.maxOrNull() ?: 0L).nanoseconds.toDouble(DurationUnit.MILLISECONDS)
    val throughputOpsPerSec: Double get() = if (averageMs == 0.0) Double.POSITIVE_INFINITY else 1_000.0 / averageMs

}

private fun <P : Any> benchmarkCompile(
    case: BenchmarkCase<P>,
    scriptCase: PerformanceScriptCase,
    settings: BenchmarkSettings,
): CompileBenchmarkResult {
    val sample = case.sample(scriptCase)

    repeat(settings.buildWarmupIterations) {
        case.withPrepared(sample) { prepared ->
            case.execute(prepared)
        }
    }

    val samples = buildList {
        repeat(settings.buildMeasuredIterations) {
            var prepared: P? = null
            val elapsed = measureNanoTime {
                prepared = case.prepare(sample)
            }
            val ready = prepared ?: error("${case.engineName} ${scriptCase.displayName} 编译阶段未返回脚本")
            try {
                case.execute(ready)
            } finally {
                case.dispose(ready)
            }
            add(elapsed)
        }
    }

    return CompileBenchmarkResult(case.engineName, scriptCase.displayName, sample.path, samples)
}

private fun <P : Any> benchmarkHot(
    case: BenchmarkCase<P>,
    scriptCase: PerformanceScriptCase,
    settings: BenchmarkSettings,
): HotBenchmarkResult {
    val sample = case.sample(scriptCase)
    val prepared = case.prepare(sample)
    try {
        case.execute(prepared)

        repeat(settings.hotWarmupIterations) {
            runExecutionBatch(case, prepared, settings.hotInvocationCount)
        }

        val samples = buildList {
            repeat(settings.hotMeasuredIterations) {
                val elapsed = measureNanoTime {
                    runExecutionBatch(case, prepared, settings.hotInvocationCount)
                }
                add(elapsed)
            }
        }

        return HotBenchmarkResult(
            engineName = case.engineName,
            scriptCaseName = scriptCase.displayName,
            samplePath = sample.path,
            samplesNs = samples,
            invocationCount = settings.hotInvocationCount,
        )
    } finally {
        case.dispose(prepared)
    }
}

private fun <P : Any> benchmarkCold(
    case: BenchmarkCase<P>,
    scriptCase: PerformanceScriptCase,
    settings: BenchmarkSettings,
): ColdBenchmarkResult {
    val sample = case.sample(scriptCase)

    repeat(settings.coldWarmupIterations) {
        case.withPrepared(sample) { prepared ->
            case.execute(prepared)
        }
    }

    val samples = buildList {
        repeat(settings.coldMeasuredIterations) {
            var prepared: P? = null
            try {
                val elapsed = measureNanoTime {
                    val ready = case.prepare(sample)
                    prepared = ready
                    case.execute(ready)
                }
                add(elapsed)
            } finally {
                prepared?.let(case::dispose)
            }
        }
    }

    return ColdBenchmarkResult(case.engineName, scriptCase.displayName, sample.path, samples)
}

private inline fun <P : Any, R> BenchmarkCase<P>.withPrepared(
    sample: ScriptSample,
    block: (P) -> R,
): R {
    val prepared = prepare(sample)
    try {
        return block(prepared)
    } finally {
        dispose(prepared)
    }
}

private fun <P : Any> runExecutionBatch(case: BenchmarkCase<P>, prepared: P, invocationCount: Int): Any? {
    var lastResult: Any? = null
    repeat(invocationCount) {
        lastResult = case.execute(prepared)
    }
    return lastResult
}

private fun renderCompileReport(results: List<CompileBenchmarkResult>, settings: BenchmarkSettings): String {
    return buildString {
        appendLine("=== module-script 编译/预处理性能测试 ===")
        appendLine("workload=fixed in samples, buildWarmup=${settings.buildWarmupIterations}, buildMeasure=${settings.buildMeasuredIterations}")
        PERFORMANCE_SCRIPT_CASES.forEach { scriptCase ->
            val caseResults = results.filter { it.scriptCaseName == scriptCase.displayName }.toMutableList().apply {
                sortWith(object : Comparator<CompileBenchmarkResult> {
                    override fun compare(left: CompileBenchmarkResult, right: CompileBenchmarkResult): Int {
                        return left.averageMs.compareTo(right.averageMs)
                    }
                })
            }
            val fastest = caseResults.firstOrNull()?.averageMs ?: 0.0
            appendLine()
            appendLine("-- ${scriptCase.displayName} --")
            appendLine(
                String.format(
                    Locale.ROOT,
                    "%4s | %-20s | %24s | %10s | %-40s",
                    "排名",
                    "引擎",
                    "avg/best/worst",
                    "相对最快",
                    "样本",
                )
            )
            appendLine("-----+----------------------+--------------------------+------------+------------------------------------------")
            caseResults.forEachIndexed { index, result ->
                appendLine(
                    String.format(
                        Locale.ROOT,
                        "%4d | %-20s | %7.3f/%7.3f/%7.3f ms | %9.2fx | %-40s",
                        index + 1,
                        result.engineName,
                        result.averageMs,
                        result.bestMs,
                        result.worstMs,
                        if (fastest == 0.0) 1.0 else result.averageMs / fastest,
                        result.samplePath,
                    )
                )
            }
        }
    }
}

private fun renderHotReport(results: List<HotBenchmarkResult>, settings: BenchmarkSettings): String {
    return buildString {
        appendLine("=== module-script 热执行性能测试 ===")
        appendLine("workload=fixed in samples, evalWarmup=${settings.hotWarmupIterations}, evalMeasure=${settings.hotMeasuredIterations}, evalInvocations=${settings.hotInvocationCount}")
        appendLine("说明：复用已准备好的脚本对象，按案例统计热执行开销。")
        PERFORMANCE_SCRIPT_CASES.forEach { scriptCase ->
            val caseResults = results.filter { it.scriptCaseName == scriptCase.displayName }.toMutableList().apply {
                sortWith(object : Comparator<HotBenchmarkResult> {
                    override fun compare(left: HotBenchmarkResult, right: HotBenchmarkResult): Int {
                        return left.averageUsPerOp.compareTo(right.averageUsPerOp)
                    }
                })
            }
            val fastest = caseResults.firstOrNull()?.averageUsPerOp ?: 0.0
            appendLine()
            appendLine("-- ${scriptCase.displayName} --")
            appendLine(
                String.format(
                    Locale.ROOT,
                    "%4s | %-20s | %28s | %10s | %14s | %-40s",
                    "排名",
                    "引擎",
                    "avg/best/worst",
                    "相对最快",
                    "吞吐(op/s)",
                    "样本",
                )
            )
            appendLine("-----+----------------------+------------------------------+------------+----------------+------------------------------------------")
            caseResults.forEachIndexed { index, result ->
                appendLine(
                    String.format(
                        Locale.ROOT,
                        "%4d | %-20s | %8.3f/%8.3f/%8.3f µs | %9.2fx | %14.0f | %-40s",
                        index + 1,
                        result.engineName,
                        result.averageUsPerOp,
                        result.bestUsPerOp,
                        result.worstUsPerOp,
                        if (fastest == 0.0) 1.0 else result.averageUsPerOp / fastest,
                        result.throughputOpsPerSec,
                        result.samplePath,
                    )
                )
            }
        }
    }
}

private fun renderColdReport(results: List<ColdBenchmarkResult>, settings: BenchmarkSettings): String {
    return buildString {
        appendLine("=== module-script 冷启动性能测试 ===")
        appendLine("workload=fixed in samples, coldWarmup=${settings.coldWarmupIterations}, coldMeasure=${settings.coldMeasuredIterations}")
        appendLine("说明：每次采样都包含 prepare + 首次 eval，用于比较单次启动成本。")
        PERFORMANCE_SCRIPT_CASES.forEach { scriptCase ->
            val caseResults = results.filter { it.scriptCaseName == scriptCase.displayName }.toMutableList().apply {
                sortWith(object : Comparator<ColdBenchmarkResult> {
                    override fun compare(left: ColdBenchmarkResult, right: ColdBenchmarkResult): Int {
                        return left.averageMs.compareTo(right.averageMs)
                    }
                })
            }
            val fastest = caseResults.firstOrNull()?.averageMs ?: 0.0
            appendLine()
            appendLine("-- ${scriptCase.displayName} --")
            appendLine(
                String.format(
                    Locale.ROOT,
                    "%4s | %-20s | %24s | %10s | %14s | %-40s",
                    "排名",
                    "引擎",
                    "avg/best/worst",
                    "相对最快",
                    "吞吐(op/s)",
                    "样本",
                )
            )
            appendLine("-----+----------------------+--------------------------+------------+----------------+------------------------------------------")
            caseResults.forEachIndexed { index, result ->
                appendLine(
                    String.format(
                        Locale.ROOT,
                        "%4d | %-20s | %7.3f/%7.3f/%7.3f ms | %9.2fx | %14.2f | %-40s",
                        index + 1,
                        result.engineName,
                        result.averageMs,
                        result.bestMs,
                        result.worstMs,
                        if (fastest == 0.0) 1.0 else result.averageMs / fastest,
                        result.throughputOpsPerSec,
                        result.samplePath,
                    )
                )
            }
        }
    }
}

