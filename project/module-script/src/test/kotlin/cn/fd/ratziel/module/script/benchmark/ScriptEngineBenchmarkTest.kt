package cn.fd.ratziel.module.script.benchmark

import cn.fd.ratziel.module.script.benchmark.engine.FluxonBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.GraalJsBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.JexlBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.KotlinScriptingBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.NashornBenchmarkCase
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.Comparator
import java.util.Locale
import kotlin.system.measureNanoTime
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit

@Tag("benchmark")
class ScriptEngineBenchmarkTest {

    @Test
    fun `benchmarks script hot execution latency`() {
        val settings = BenchmarkSettings.fromSystemProperties()
        val execution = collectHotResults(settings)

        println(renderHotReport(execution.results, settings))
        if (execution.failures.isNotEmpty()) {
            println(renderFailureReport(execution.failures))
            fail("热执行基准存在 ${execution.failures.size} 个失败项")
        }
    }

    @Test
    fun `benchmarks script cold startup latency`() {
        val settings = BenchmarkSettings.fromSystemProperties()
        val execution = collectColdResults(settings)

        println(renderColdReport(execution.results, settings))
        if (execution.failures.isNotEmpty()) {
            println(renderFailureReport(execution.failures))
            fail("冷启动基准存在 ${execution.failures.size} 个失败项")
        }
    }

    @Test
    fun `benchmarks script compile latency`() {
        val settings = BenchmarkSettings.fromSystemProperties()
        val execution = collectCompileResults(settings)

        println(renderCompileReport(execution.results, settings))
        if (execution.failures.isNotEmpty()) {
            println(renderFailureReport(execution.failures))
            fail("编译基准存在 ${execution.failures.size} 个失败项")
        }
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

private enum class BenchmarkPhase(val displayName: String) {
    COMPILE("编译/预处理"),
    HOT("热执行"),
    COLD("冷启动"),
}

private data class BenchmarkExecution<T>(
    val results: List<T>,
    val failures: List<BenchmarkFailure>,
)

private data class BenchmarkFailure(
    val phase: BenchmarkPhase,
    val engineName: String,
    val scriptCaseName: String,
    val samplePath: String,
    val throwable: Throwable,
)

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

private fun collectCompileResults(settings: BenchmarkSettings): BenchmarkExecution<CompileBenchmarkResult> {
    val results = mutableListOf<CompileBenchmarkResult>()
    val failures = mutableListOf<BenchmarkFailure>()
    BENCHMARK_SCRIPT_CASES.forEach { scriptCase ->
        BENCHMARK_CASES.filter { it.supports(scriptCase) }.forEach { case ->
            collectResult(case, scriptCase, BenchmarkPhase.COMPILE, results, failures) {
                benchmarkCompile(it, scriptCase, settings)
            }
        }
    }
    return BenchmarkExecution(results, failures)
}

private fun collectHotResults(settings: BenchmarkSettings): BenchmarkExecution<HotBenchmarkResult> {
    val results = mutableListOf<HotBenchmarkResult>()
    val failures = mutableListOf<BenchmarkFailure>()
    BENCHMARK_SCRIPT_CASES.forEach { scriptCase ->
        BENCHMARK_CASES.filter { it.supports(scriptCase) }.forEach { case ->
            collectResult(case, scriptCase, BenchmarkPhase.HOT, results, failures) {
                benchmarkHot(it, scriptCase, settings)
            }
        }
    }
    return BenchmarkExecution(results, failures)
}

private fun collectColdResults(settings: BenchmarkSettings): BenchmarkExecution<ColdBenchmarkResult> {
    val results = mutableListOf<ColdBenchmarkResult>()
    val failures = mutableListOf<BenchmarkFailure>()
    BENCHMARK_SCRIPT_CASES.forEach { scriptCase ->
        BENCHMARK_CASES.filter { it.supports(scriptCase) }.forEach { case ->
            collectResult(case, scriptCase, BenchmarkPhase.COLD, results, failures) {
                benchmarkCold(it, scriptCase, settings)
            }
        }
    }
    return BenchmarkExecution(results, failures)
}

private inline fun <P : Any, R> collectResult(
    case: BenchmarkCase<P>,
    scriptCase: BenchmarkScriptCase,
    phase: BenchmarkPhase,
    results: MutableList<R>,
    failures: MutableList<BenchmarkFailure>,
    block: (BenchmarkCase<P>) -> R,
) {
    runCatching {
        block(case)
    }.onSuccess(results::add)
        .onFailure { throwable ->
            failures += BenchmarkFailure(
                phase = phase,
                engineName = case.engineName,
                scriptCaseName = scriptCase.displayName,
                samplePath = case.samplePathOf(scriptCase),
                throwable = throwable,
            )
        }
}

private fun <P : Any> BenchmarkCase<P>.samplePathOf(scriptCase: BenchmarkScriptCase): String {
    return samples[scriptCase.id]?.path ?: scriptCase.fileNames.entries.firstOrNull()?.value ?: scriptCase.id
}

private fun <P : Any> benchmarkCompile(
    case: BenchmarkCase<P>,
    scriptCase: BenchmarkScriptCase,
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
    scriptCase: BenchmarkScriptCase,
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
    scriptCase: BenchmarkScriptCase,
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
        appendLine("=== 脚本模块 编译/预处理基准测试 ===")
        appendLine("workload=fixed in samples, buildWarmup=${settings.buildWarmupIterations}, buildMeasure=${settings.buildMeasuredIterations}")
        BENCHMARK_SCRIPT_CASES.forEach { scriptCase ->
            val caseResults = results.filter { it.scriptCaseName == scriptCase.displayName }.toMutableList().apply {
                sortWith(object : Comparator<CompileBenchmarkResult> {
                    override fun compare(left: CompileBenchmarkResult, right: CompileBenchmarkResult): Int {
                        return left.averageMs.compareTo(right.averageMs)
                    }
                })
            }
            appendLine()
            appendLine("-- ${scriptCase.displayName} --")
            if (caseResults.isEmpty()) {
                appendLine("无成功结果")
                return@forEach
            }
            val fastest = caseResults.first().averageMs
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
        appendLine("=== 脚本模块 热执行基准测试 ===")
        appendLine("workload=fixed in samples, evalWarmup=${settings.hotWarmupIterations}, evalMeasure=${settings.hotMeasuredIterations}, evalInvocations=${settings.hotInvocationCount}")
        appendLine("说明：复用已准备好的脚本对象，按案例统计热执行开销。")
        BENCHMARK_SCRIPT_CASES.forEach { scriptCase ->
            val caseResults = results.filter { it.scriptCaseName == scriptCase.displayName }.toMutableList().apply {
                sortWith(object : Comparator<HotBenchmarkResult> {
                    override fun compare(left: HotBenchmarkResult, right: HotBenchmarkResult): Int {
                        return left.averageUsPerOp.compareTo(right.averageUsPerOp)
                    }
                })
            }
            appendLine()
            appendLine("-- ${scriptCase.displayName} --")
            if (caseResults.isEmpty()) {
                appendLine("无成功结果")
                return@forEach
            }
            val fastest = caseResults.first().averageUsPerOp
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
        appendLine("=== 脚本模块 冷启动基准测试 ===")
        appendLine("workload=fixed in samples, coldWarmup=${settings.coldWarmupIterations}, coldMeasure=${settings.coldMeasuredIterations}")
        appendLine("说明：每次采样都包含 prepare + 首次 eval，用于比较单次启动成本。")
        BENCHMARK_SCRIPT_CASES.forEach { scriptCase ->
            val caseResults = results.filter { it.scriptCaseName == scriptCase.displayName }.toMutableList().apply {
                sortWith(object : Comparator<ColdBenchmarkResult> {
                    override fun compare(left: ColdBenchmarkResult, right: ColdBenchmarkResult): Int {
                        return left.averageMs.compareTo(right.averageMs)
                    }
                })
            }
            appendLine()
            appendLine("-- ${scriptCase.displayName} --")
            if (caseResults.isEmpty()) {
                appendLine("无成功结果")
                return@forEach
            }
            val fastest = caseResults.first().averageMs
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

private fun renderFailureReport(failures: List<BenchmarkFailure>): String {
    return buildString {
        appendLine("=== 失败项汇总 ===")
        failures.forEach { failure ->
            appendLine(
                String.format(
                    Locale.ROOT,
                    "[%s] %s | %s | %s | %s",
                    failure.phase.displayName,
                    failure.engineName,
                    failure.scriptCaseName,
                    failure.samplePath,
                    failure.throwable.renderSummary(),
                )
            )
        }
    }
}

private fun Throwable.renderSummary(): String {
    val message = message?.replace('\n', ' ')?.takeIf { it.isNotBlank() } ?: "无详细信息"
    return "${this::class.qualifiedName}: $message"
}
