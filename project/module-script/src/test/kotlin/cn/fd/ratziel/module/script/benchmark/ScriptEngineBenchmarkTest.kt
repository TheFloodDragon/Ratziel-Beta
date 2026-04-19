package cn.fd.ratziel.module.script.benchmark

import cn.fd.ratziel.module.script.benchmark.engine.FluxonBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.GraalJsBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.JexlBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.KotlinScriptingBenchmarkCase
import cn.fd.ratziel.module.script.benchmark.engine.NashornBenchmarkCase
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.system.measureNanoTime

@Tag("benchmark")
@Execution(ExecutionMode.CONCURRENT)
class ScriptEngineBenchmarkTest {

    @Test
    fun `benchmarks script compiled execution latency`() {
        runBenchmark(
            collect = ::collectCompiledExecutionResults,
            render = ::renderCompiledExecutionReport,
            failureMessage = { "编译运行基准存在 $it 个失败项" },
        )
    }

    @Test
    fun `benchmarks script interpreted execution latency`() {
        runBenchmark(
            collect = ::collectInterpretedExecutionResults,
            render = ::renderInterpretedExecutionReport,
            failureMessage = { "解释运行基准存在 $it 个失败项" },
        )
    }

    @Test
    fun `benchmarks script compile latency`() {
        runBenchmark(
            collect = ::collectCompileResults,
            render = ::renderCompileReport,
            failureMessage = { "编译基准存在 $it 个失败项" },
        )
    }
}

private fun <R> runBenchmark(
    collect: (BenchmarkSettings) -> BenchmarkExecution<R>,
    render: (List<R>, BenchmarkSettings) -> String,
    failureMessage: (Int) -> String,
) {
    val settings = BENCHMARK_SETTINGS
    val execution = collect(settings)

    println(render(execution.results, settings))
    if (execution.failures.isNotEmpty()) {
        println(renderFailureReport(execution.failures))
        throw AssertionError(failureMessage(execution.failures.size))
    }
}

private val BENCHMARK_CASES: List<BenchmarkCase<out Any>> = listOf(
    GraalJsBenchmarkCase,
    NashornBenchmarkCase,
    JexlBenchmarkCase,
    KotlinScriptingBenchmarkCase,
    FluxonBenchmarkCase,
)

private enum class BenchmarkPhase(val displayName: String) {
    COMPILE("编译"),
    COMPILED_EXECUTION("编译运行"),
    INTERPRETED_EXECUTION("解释运行"),
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

private data class BenchmarkMetrics(
    val forkScoresNs: List<Double>,
    val measuredSamplesNs: List<Long>,
    val operationsPerSample: Int = 1,
) {

    val scoreNs: Double get() = forkScoresNs.averageOrZero()
    val errorNs: Double get() = forkScoresNs.standardDeviation()
    val bestNs: Double get() = measuredSamplesNs.minOrNull()?.toDouble() ?: 0.0
    val medianNs: Double get() = measuredSamplesNs.percentile(0.5)
    val p90Ns: Double get() = measuredSamplesNs.percentile(0.9)
    val worstNs: Double get() = measuredSamplesNs.maxOrNull()?.toDouble() ?: 0.0
    val throughputOpsPerSec: Double get() = scoreNs.toOpsPerSecond(operationsPerSample)
}

private data class CompileBenchmarkResult(
    val engineName: String,
    val scriptCaseName: String,
    val samplePath: String,
    val metrics: BenchmarkMetrics,
) {

    val scoreMs: Double get() = metrics.scoreNs.toMilliseconds()
    val errorMs: Double get() = metrics.errorNs.toMilliseconds()
    val bestMs: Double get() = metrics.bestNs.toMilliseconds()
    val medianMs: Double get() = metrics.medianNs.toMilliseconds()
    val p90Ms: Double get() = metrics.p90Ns.toMilliseconds()
    val worstMs: Double get() = metrics.worstNs.toMilliseconds()
}

private data class CompiledExecutionBenchmarkResult(
    val engineName: String,
    val scriptCaseName: String,
    val samplePath: String,
    val metrics: BenchmarkMetrics,
) {

    val scoreUsPerOp: Double get() = metrics.scoreNs.toMicroseconds() / metrics.operationsPerSample
    val errorUsPerOp: Double get() = metrics.errorNs.toMicroseconds() / metrics.operationsPerSample
    val bestUsPerOp: Double get() = metrics.bestNs.toMicroseconds() / metrics.operationsPerSample
    val medianUsPerOp: Double get() = metrics.medianNs.toMicroseconds() / metrics.operationsPerSample
    val p90UsPerOp: Double get() = metrics.p90Ns.toMicroseconds() / metrics.operationsPerSample
    val worstUsPerOp: Double get() = metrics.worstNs.toMicroseconds() / metrics.operationsPerSample
    val throughputOpsPerSec: Double get() = metrics.throughputOpsPerSec
}

private data class InterpretedExecutionBenchmarkResult(
    val engineName: String,
    val scriptCaseName: String,
    val samplePath: String,
    val metrics: BenchmarkMetrics,
) {

    val scoreMs: Double get() = metrics.scoreNs.toMilliseconds()
    val errorMs: Double get() = metrics.errorNs.toMilliseconds()
    val bestMs: Double get() = metrics.bestNs.toMilliseconds()
    val medianMs: Double get() = metrics.medianNs.toMilliseconds()
    val p90Ms: Double get() = metrics.p90Ns.toMilliseconds()
    val worstMs: Double get() = metrics.worstNs.toMilliseconds()
    val throughputOpsPerSec: Double get() = metrics.throughputOpsPerSec
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

private fun collectCompiledExecutionResults(settings: BenchmarkSettings): BenchmarkExecution<CompiledExecutionBenchmarkResult> {
    val results = mutableListOf<CompiledExecutionBenchmarkResult>()
    val failures = mutableListOf<BenchmarkFailure>()
    BENCHMARK_SCRIPT_CASES.forEach { scriptCase ->
        BENCHMARK_CASES.filter { it.supports(scriptCase) }.forEach { case ->
            collectResult(case, scriptCase, BenchmarkPhase.COMPILED_EXECUTION, results, failures) {
                benchmarkCompiledExecution(it, scriptCase, settings)
            }
        }
    }
    return BenchmarkExecution(results, failures)
}

private fun collectInterpretedExecutionResults(settings: BenchmarkSettings): BenchmarkExecution<InterpretedExecutionBenchmarkResult> {
    val results = mutableListOf<InterpretedExecutionBenchmarkResult>()
    val failures = mutableListOf<BenchmarkFailure>()
    BENCHMARK_SCRIPT_CASES.forEach { scriptCase ->
        BENCHMARK_CASES.filter { it.supports(scriptCase) }.forEach { case ->
            collectResult(case, scriptCase, BenchmarkPhase.INTERPRETED_EXECUTION, results, failures) {
                benchmarkInterpretedExecution(it, scriptCase, settings)
            }
        }
    }
    return BenchmarkExecution(results, failures)
}

private fun <R> collectResult(
    case: BenchmarkCase<out Any>,
    scriptCase: BenchmarkScriptCase,
    phase: BenchmarkPhase,
    results: MutableList<R>,
    failures: MutableList<BenchmarkFailure>,
    block: (BenchmarkCase<Any>) -> R,
) {
    @Suppress("UNCHECKED_CAST")
    val typedCase = case as BenchmarkCase<Any>
    runCatching {
        block(typedCase)
    }.onSuccess(results::add)
        .onFailure { throwable ->
            failures += BenchmarkFailure(
                phase = phase,
                engineName = typedCase.engineName,
                scriptCaseName = scriptCase.displayName,
                samplePath = typedCase.samplePathOf(scriptCase),
                throwable = throwable,
            )
        }
}

private fun BenchmarkCase<*>.samplePathOf(scriptCase: BenchmarkScriptCase): String {
    return samples[scriptCase.id]?.path ?: scriptCase.id
}

private fun benchmarkCompile(
    case: BenchmarkCase<Any>,
    scriptCase: BenchmarkScriptCase,
    settings: BenchmarkSettings,
): CompileBenchmarkResult {
    val sample = case.sample(scriptCase)
    val metrics = measureBenchmark(
        forks = settings.forks,
        warmupIterations = settings.buildWarmupIterations,
        measuredIterations = settings.buildMeasuredIterations,
        createState = { Unit },
        runIteration = {
            var compiled: Any? = null
            val elapsed = measureNanoTime {
                compiled = case.compile(sample)
            }
            val ready = compiled ?: error("${case.engineName} ${scriptCase.displayName} 编译阶段未返回结果")
            try {
                case.runCompiled(ready)
            } finally {
                case.disposeCompiled(ready)
            }
            elapsed
        },
    )
    return CompileBenchmarkResult(case.engineName, scriptCase.displayName, sample.path, metrics)
}

private fun benchmarkCompiledExecution(
    case: BenchmarkCase<Any>,
    scriptCase: BenchmarkScriptCase,
    settings: BenchmarkSettings,
): CompiledExecutionBenchmarkResult {
    val sample = case.sample(scriptCase)
    val metrics = measureBenchmark(
        forks = settings.forks,
        warmupIterations = settings.compiledExecutionWarmupIterations,
        measuredIterations = settings.compiledExecutionMeasuredIterations,
        createState = {
            case.compile(sample)
        },
        destroyState = case::disposeCompiled,
        runIteration = { compiled ->
            measureNanoTime {
                runExecutionBatch(case, compiled, settings.iterations)
            }
        },
        operationsPerSample = settings.iterations,
    )
    return CompiledExecutionBenchmarkResult(case.engineName, scriptCase.displayName, sample.path, metrics)
}

private fun benchmarkInterpretedExecution(
    case: BenchmarkCase<Any>,
    scriptCase: BenchmarkScriptCase,
    settings: BenchmarkSettings,
): InterpretedExecutionBenchmarkResult {
    val sample = case.sample(scriptCase)
    val metrics = measureBenchmark(
        forks = settings.forks,
        warmupIterations = settings.interpretedExecutionWarmupIterations,
        measuredIterations = settings.interpretedExecutionMeasuredIterations,
        createState = { Unit },
        runIteration = {
            measureNanoTime {
                case.interpret(sample)
            }
        },
    )
    return InterpretedExecutionBenchmarkResult(case.engineName, scriptCase.displayName, sample.path, metrics)
}

private inline fun <S> measureBenchmark(
    forks: Int,
    warmupIterations: Int,
    measuredIterations: Int,
    createState: (Int) -> S,
    runIteration: (S) -> Long,
    destroyState: (S) -> Unit = {},
    operationsPerSample: Int = 1,
): BenchmarkMetrics {
    val forkScores = mutableListOf<Double>()
    val measuredSamples = mutableListOf<Long>()

    repeat(forks) { forkIndex ->
        val state = createState(forkIndex)
        try {
            repeat(warmupIterations) {
                runIteration(state)
            }

            val forkSamples = buildList {
                repeat(measuredIterations) {
                    add(runIteration(state))
                }
            }
            measuredSamples += forkSamples
            forkScores += forkSamples.averageOrZero()
        } finally {
            destroyState(state)
        }
    }

    return BenchmarkMetrics(
        forkScoresNs = forkScores,
        measuredSamplesNs = measuredSamples,
        operationsPerSample = operationsPerSample,
    )
}

private fun runExecutionBatch(case: BenchmarkCase<Any>, compiled: Any, invocationCount: Int): Any? {
    var lastResult: Any? = null
    repeat(invocationCount) {
        lastResult = case.runCompiled(compiled)
    }
    return lastResult
}

private fun renderCompileReport(results: List<CompileBenchmarkResult>, settings: BenchmarkSettings): String {
    return buildString {
        appendLine("=== 脚本模块 编译基准测试 ===")
        appendLine("forks=${settings.forks}, warmup=${settings.buildWarmupIterations}, measure=${settings.buildMeasuredIterations}")
        appendLine("说明：仅统计 compile() 耗时；验证执行与资源释放均放在计时外。score/error 按 fork 聚合，best/p50/p90/worst 来自全部测量样本。")
        BENCHMARK_SCRIPT_CASES.forEach { scriptCase ->
            val caseResults = results.filter { it.scriptCaseName == scriptCase.displayName }
                .sortedBy { it.scoreMs }
            appendLine()
            appendLine("-- ${scriptCase.displayName} --")
            if (caseResults.isEmpty()) {
                appendLine("无成功结果")
                return@forEach
            }
            val fastest = caseResults.first().scoreMs
            appendLine(
                String.format(
                    Locale.ROOT,
                    "%4s | %-18s | %17s | %23s | %9s | %10s | %-40s",
                    "排名",
                    "引擎",
                    "score±err",
                    "best/p50/p90",
                    "worst",
                    "相对最快",
                    "样本",
                )
            )
            appendLine("-----+--------------------+-------------------+-------------------------+-----------+------------+------------------------------------------")
            caseResults.forEachIndexed { index, result ->
                appendLine(
                    String.format(
                        Locale.ROOT,
                        "%4d | %-18s | %8.3f±%-7.3f | %7.3f/%7.3f/%7.3f | %7.3f ms | %9.2fx | %-40s",
                        index + 1,
                        result.engineName,
                        result.scoreMs,
                        result.errorMs,
                        result.bestMs,
                        result.medianMs,
                        result.p90Ms,
                        result.worstMs,
                        if (fastest == 0.0) 1.0 else result.scoreMs / fastest,
                        result.samplePath,
                    )
                )
            }
        }
    }
}

private fun renderCompiledExecutionReport(results: List<CompiledExecutionBenchmarkResult>, settings: BenchmarkSettings): String {
    return buildString {
        appendLine("=== 脚本模块 编译运行基准测试 ===")
        appendLine("forks=${settings.forks}, warmup=${settings.compiledExecutionWarmupIterations}, measure=${settings.compiledExecutionMeasuredIterations}, batchOps=${settings.iterations}")
        appendLine("说明：每个 fork 先 compile() 一次，再对同一编译结果重复执行；score/error 为 µs/op，吞吐(op/s)按 batchOps 换算。")
        BENCHMARK_SCRIPT_CASES.forEach { scriptCase ->
            val caseResults = results.filter { it.scriptCaseName == scriptCase.displayName }
                .sortedBy { it.scoreUsPerOp }
            appendLine()
            appendLine("-- ${scriptCase.displayName} --")
            if (caseResults.isEmpty()) {
                appendLine("无成功结果")
                return@forEach
            }
            val fastest = caseResults.first().scoreUsPerOp
            appendLine(
                String.format(
                    Locale.ROOT,
                    "%4s | %-18s | %17s | %23s | %10s | %14s | %10s | %-40s",
                    "排名",
                    "引擎",
                    "score±err",
                    "best/p50/p90",
                    "worst",
                    "吞吐(op/s)",
                    "相对最快",
                    "样本",
                )
            )
            appendLine("-----+--------------------+-------------------+-------------------------+------------+----------------+------------+------------------------------------------")
            caseResults.forEachIndexed { index, result ->
                appendLine(
                    String.format(
                        Locale.ROOT,
                        "%4d | %-18s | %8.3f±%-7.3f | %7.3f/%7.3f/%7.3f | %8.3f µs | %14.0f | %9.2fx | %-40s",
                        index + 1,
                        result.engineName,
                        result.scoreUsPerOp,
                        result.errorUsPerOp,
                        result.bestUsPerOp,
                        result.medianUsPerOp,
                        result.p90UsPerOp,
                        result.worstUsPerOp,
                        result.throughputOpsPerSec,
                        if (fastest == 0.0) 1.0 else result.scoreUsPerOp / fastest,
                        result.samplePath,
                    )
                )
            }
        }
    }
}

private fun renderInterpretedExecutionReport(results: List<InterpretedExecutionBenchmarkResult>, settings: BenchmarkSettings): String {
    return buildString {
        appendLine("=== 脚本模块 解释运行基准测试 ===")
        appendLine("forks=${settings.forks}, warmup=${settings.interpretedExecutionWarmupIterations}, measure=${settings.interpretedExecutionMeasuredIterations}")
        appendLine("说明：解释运行只统计 interpret() 直接运行路径，不再 fallback 到 compile()+run；吞吐(op/s)按每次采样 1 次操作换算。")
        BENCHMARK_SCRIPT_CASES.forEach { scriptCase ->
            val caseResults = results.filter { it.scriptCaseName == scriptCase.displayName }
                .sortedBy { it.scoreMs }
            appendLine()
            appendLine("-- ${scriptCase.displayName} --")
            if (caseResults.isEmpty()) {
                appendLine("无成功结果")
                return@forEach
            }
            val fastest = caseResults.first().scoreMs
            appendLine(
                String.format(
                    Locale.ROOT,
                    "%4s | %-18s | %17s | %23s | %9s | %14s | %10s | %-40s",
                    "排名",
                    "引擎",
                    "score±err",
                    "best/p50/p90",
                    "worst",
                    "吞吐(op/s)",
                    "相对最快",
                    "样本",
                )
            )
            appendLine("-----+--------------------+-------------------+-------------------------+-----------+----------------+------------+------------------------------------------")
            caseResults.forEachIndexed { index, result ->
                appendLine(
                    String.format(
                        Locale.ROOT,
                        "%4d | %-18s | %8.3f±%-7.3f | %7.3f/%7.3f/%7.3f | %7.3f ms | %14.2f | %9.2fx | %-40s",
                        index + 1,
                        result.engineName,
                        result.scoreMs,
                        result.errorMs,
                        result.bestMs,
                        result.medianMs,
                        result.p90Ms,
                        result.worstMs,
                        result.throughputOpsPerSec,
                        if (fastest == 0.0) 1.0 else result.scoreMs / fastest,
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

private fun List<Long>.averageOrZero(): Double {
    return if (isEmpty()) 0.0 else average()
}

private fun List<Double>.averageOrZero(): Double {
    return if (isEmpty()) 0.0 else average()
}

private fun List<Double>.standardDeviation(): Double {
    if (isEmpty()) return 0.0
    val mean = average()
    val variance = sumOf { value ->
        val delta = value - mean
        delta * delta
    } / size
    return sqrt(variance)
}

private fun List<Long>.percentile(ratio: Double): Double {
    if (isEmpty()) return 0.0
    val sorted = sorted()
    val position = ratio.coerceIn(0.0, 1.0) * (sorted.size - 1)
    val lowerIndex = floor(position).toInt()
    val upperIndex = ceil(position).toInt()
    if (lowerIndex == upperIndex) {
        return sorted[lowerIndex].toDouble()
    }
    val weight = position - lowerIndex
    return sorted[lowerIndex].toDouble() + (sorted[upperIndex] - sorted[lowerIndex]).toDouble() * weight
}

private fun Double.toMilliseconds(): Double = this / 1_000_000.0

private fun Double.toMicroseconds(): Double = this / 1_000.0

private fun Double.toOpsPerSecond(operationCount: Int): Double {
    if (this == 0.0) return Double.POSITIVE_INFINITY
    return operationCount * 1_000_000_000.0 / this
}
