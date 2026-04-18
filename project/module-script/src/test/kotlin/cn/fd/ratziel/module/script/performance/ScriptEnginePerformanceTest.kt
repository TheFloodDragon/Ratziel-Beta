package cn.fd.ratziel.module.script.performance

import cn.fd.ratziel.module.script.api.CompiledScript as ModuleCompiledScript
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptSource
import cn.fd.ratziel.module.script.conf.ScriptConfiguration
import cn.fd.ratziel.module.script.conf.ScriptConfigurationKeys
import cn.fd.ratziel.module.script.conf.scriptCaching
import cn.fd.ratziel.module.script.lang.fluxon.FluxonLang
import cn.fd.ratziel.module.script.lang.fluxon.FluxonScriptExecutor
import cn.fd.ratziel.module.script.lang.kts.KtsJvmHost
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlFeatures
import org.apache.commons.jexl3.JexlScript
import org.apache.commons.jexl3.MapContext
import org.apache.commons.jexl3.introspection.JexlPermissions
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Engine
import org.graalvm.polyglot.Source
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import java.util.Comparator
import java.util.Locale
import javax.script.Compilable
import javax.script.CompiledScript as Jsr223CompiledScript
import javax.script.ScriptEngine
import kotlin.script.experimental.api.CompiledScript as KotlinCompiledScript
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.system.measureNanoTime
import kotlin.test.assertEquals
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

private const val FIXED_LIMIT = 100_000L
private const val FIXED_COLLECTION_SIZE = 2_048
private const val FIXED_STRING_SIZE = 4_096

private val EXPECTED_COMPUTE_RESULT = (FIXED_LIMIT - 1) * FIXED_LIMIT / 2
private val EXPECTED_BRANCHING_RESULT by lazy {
    var expected = 0L
    for (i in 0L until FIXED_LIMIT) {
        expected = when {
            i % 2L == 0L -> expected + i
            i % 3L == 0L -> expected - i
            else -> expected + 1L
        }
    }
    expected
}

private val PERFORMANCE_SCRIPT_CASES = listOf(
    PerformanceScriptCase(
        id = "compute",
        displayName = "数值累加",
        fileNames = mapOf(
            ScriptDialect.JavaScript to "compute.js",
            ScriptDialect.Jexl to "compute.jexl",
            ScriptDialect.Kotlin to "compute.benchmark.kts",
            ScriptDialect.Fluxon to "compute.fs",
        ),
        verifier = { value, engineName ->
            assertEquals(EXPECTED_COMPUTE_RESULT, (value as? Number)?.toLong(), "$engineName 数值累加 结果异常")
        },
    ),
    PerformanceScriptCase(
        id = "branching",
        displayName = "条件分支",
        fileNames = mapOf(
            ScriptDialect.JavaScript to "branching.js",
            ScriptDialect.Jexl to "branching.jexl",
            ScriptDialect.Kotlin to "branching.benchmark.kts",
            ScriptDialect.Fluxon to "branching.fs",
        ),
        verifier = { value, engineName ->
            assertEquals(EXPECTED_BRANCHING_RESULT, (value as? Number)?.toLong(), "$engineName 条件分支 结果异常")
        },
    ),
    PerformanceScriptCase(
        id = "list-build",
        displayName = "列表构建",
        fileNames = mapOf(
            ScriptDialect.JavaScript to "list-build.js",
            ScriptDialect.Jexl to "list-build.jexl",
            ScriptDialect.Kotlin to "list-build.benchmark.kts",
            ScriptDialect.Fluxon to "list-build.fs",
        ),
        verifier = { value, engineName ->
            assertEquals(FIXED_COLLECTION_SIZE, (value as? Collection<*>)?.size, "$engineName 列表构建 结果异常")
        },
    ),
    PerformanceScriptCase(
        id = "map-build",
        displayName = "映射构建",
        fileNames = mapOf(
            ScriptDialect.JavaScript to "map-build.js",
            ScriptDialect.Jexl to "map-build.jexl",
            ScriptDialect.Kotlin to "map-build.benchmark.kts",
            ScriptDialect.Fluxon to "map-build.fs",
        ),
        verifier = { value, engineName ->
            assertEquals(FIXED_COLLECTION_SIZE, (value as? Map<*, *>)?.size, "$engineName 映射构建 结果异常")
        },
    ),
    PerformanceScriptCase(
        id = "string-build",
        displayName = "字符串构建",
        fileNames = mapOf(
            ScriptDialect.JavaScript to "string-build.js",
            ScriptDialect.Jexl to "string-build.jexl",
            ScriptDialect.Kotlin to "string-build.benchmark.kts",
            ScriptDialect.Fluxon to "string-build.fs",
        ),
        verifier = { value, engineName ->
            assertEquals(FIXED_STRING_SIZE, (value as? String)?.length, "$engineName 字符串构建 结果异常")
        },
    ),
)

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

private enum class ScriptDialect(val directory: String) {
    JavaScript("javascript"),
    Jexl("jexl"),
    Kotlin("kotlin"),
    Fluxon("fluxon"),
}

private data class ScriptSample(
    val path: String,
    val content: String,
)

private data class PerformanceScriptCase(
    val id: String,
    val displayName: String,
    val fileNames: Map<ScriptDialect, String>,
    val verifier: (value: Any?, engineName: String) -> Unit,
) {

    fun fileName(dialect: ScriptDialect): String = fileNames.getValue(dialect)

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

private interface BenchmarkCase<P : Any> {

    val engineName: String

    val dialect: ScriptDialect

    val samples: Map<String, ScriptSample>

    fun supports(scriptCase: PerformanceScriptCase): Boolean = samples.containsKey(scriptCase.id)

    fun sample(scriptCase: PerformanceScriptCase): ScriptSample = samples.getValue(scriptCase.id)

    fun prepare(sample: ScriptSample): P

    fun execute(prepared: P): Any?

    fun dispose(prepared: P) = Unit

}

private fun <P : Any> benchmarkCompile(
    case: BenchmarkCase<P>,
    scriptCase: PerformanceScriptCase,
    settings: BenchmarkSettings,
): CompileBenchmarkResult {
    val sample = case.sample(scriptCase)

    repeat(settings.buildWarmupIterations) {
        case.withPrepared(sample) { prepared ->
            scriptCase.verifier(case.execute(prepared), case.engineName)
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
                scriptCase.verifier(case.execute(ready), case.engineName)
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
        scriptCase.verifier(case.execute(prepared), case.engineName)

        repeat(settings.hotWarmupIterations) {
            val lastResult = runExecutionBatch(case, prepared, settings.hotInvocationCount)
            scriptCase.verifier(lastResult, case.engineName)
        }

        val samples = buildList {
            repeat(settings.hotMeasuredIterations) {
                var lastResult: Any? = null
                val elapsed = measureNanoTime {
                    lastResult = runExecutionBatch(case, prepared, settings.hotInvocationCount)
                }
                scriptCase.verifier(lastResult, case.engineName)
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
            scriptCase.verifier(case.execute(prepared), case.engineName)
        }
    }

    val samples = buildList {
        repeat(settings.coldMeasuredIterations) {
            var prepared: P? = null
            var result: Any? = null
            val elapsed = measureNanoTime {
                val ready = case.prepare(sample)
                prepared = ready
                result = case.execute(ready)
            }
            val ready = prepared ?: error("${case.engineName} ${scriptCase.displayName} 冷启动阶段未返回脚本")
            try {
                scriptCase.verifier(result, case.engineName)
            } finally {
                case.dispose(ready)
            }
            add(elapsed)
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

private object GraalJsBenchmarkCase : BenchmarkCase<GraalPreparedScript> {

    private val sharedEngine: Engine = Engine.newBuilder("js")
        .allowExperimentalOptions(true)
        .option("js.ecmascript-version", "latest")
        .option("js.nashorn-compat", "true")
        .build()

    override val engineName: String = "GraalJS"

    override val dialect: ScriptDialect = ScriptDialect.JavaScript

    override val samples: Map<String, ScriptSample> = engineSamples(dialect)

    override fun prepare(sample: ScriptSample): GraalPreparedScript {
        val context = Context.newBuilder("js")
            .allowAllAccess(true)
            .engine(sharedEngine)
            .build()
        val source = Source.newBuilder("js", sample.content, sample.path)
            .cached(true)
            .build()
        return GraalPreparedScript(context, source)
    }

    override fun execute(prepared: GraalPreparedScript): Any? {
        return prepared.context.eval(prepared.source).`as`(Any::class.java)
    }

    override fun dispose(prepared: GraalPreparedScript) {
        prepared.close()
    }

}

private data class GraalPreparedScript(
    val context: Context,
    val source: Source,
) : AutoCloseable {
    override fun close() {
        context.close()
    }
}

private object NashornBenchmarkCase : BenchmarkCase<NashornPreparedScript> {

    private val factory = NashornScriptEngineFactory()

    override val engineName: String = "Nashorn (JSR223)"

    override val dialect: ScriptDialect = ScriptDialect.JavaScript

    override val samples: Map<String, ScriptSample> = engineSamples(dialect)

    override fun prepare(sample: ScriptSample): NashornPreparedScript {
        val engine = factory.getScriptEngine(
            arrayOf("-Dnashorn.args=--language=es6"),
            this::class.java.classLoader,
        )
        val compiled = (engine as Compilable).compile(sample.content)
        return NashornPreparedScript(engine, compiled)
    }

    override fun execute(prepared: NashornPreparedScript): Any? {
        return prepared.script.eval(prepared.engine.context)
    }

}

private data class NashornPreparedScript(
    val engine: ScriptEngine,
    val script: Jsr223CompiledScript,
)

private object JexlBenchmarkCase : BenchmarkCase<JexlPreparedScript> {

    private val engine = JexlBuilder()
        .cache(16)
        .strict(true)
        .features(JexlFeatures.createAll())
        .permissions(JexlPermissions.UNRESTRICTED)
        .create()

    override val engineName: String = "JEXL"

    override val dialect: ScriptDialect = ScriptDialect.Jexl

    override val samples: Map<String, ScriptSample> = engineSamples(dialect)

    override fun prepare(sample: ScriptSample): JexlPreparedScript {
        val script = engine.createScript(sample.content)
        return JexlPreparedScript(MapContext(), script)
    }

    override fun execute(prepared: JexlPreparedScript): Any? {
        return prepared.script.execute(prepared.context)
    }

}

private data class JexlPreparedScript(
    val context: MapContext,
    val script: JexlScript,
)

private object KotlinScriptingBenchmarkCase : BenchmarkCase<KotlinPreparedScript> {

    private val host = KtsJvmHost()

    override val engineName: String = "KotlinScripting"

    override val dialect: ScriptDialect = ScriptDialect.Kotlin

    override val samples: Map<String, ScriptSample> = engineSamples(dialect)

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

private data class KotlinPreparedScript(
    val host: KtsJvmHost,
    val script: KotlinCompiledScript,
    val evaluationConfiguration: ScriptEvaluationConfiguration,
)

object KotlinPerformanceCompilationConfiguration : ScriptCompilationConfiguration({
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
})

object KotlinPerformanceEvaluationConfiguration : ScriptEvaluationConfiguration({
})

private object FluxonBenchmarkCase : BenchmarkCase<FluxonPreparedScript> {

    private val configuration = ScriptConfiguration {
        this[ScriptConfigurationKeys.scriptCaching] = 2
    }

    private val executor = FluxonScriptExecutor().apply {
        this.configuration = this@FluxonBenchmarkCase.configuration
    }

    override val engineName: String = "Fluxon"

    override val dialect: ScriptDialect = ScriptDialect.Fluxon

    override val samples: Map<String, ScriptSample> = engineSamples(
        dialect,
        PERFORMANCE_SCRIPT_CASES.filter { it.id != "map-build" },
    )

    override fun prepare(sample: ScriptSample): FluxonPreparedScript {
        val environment = ScriptEnvironment(configuration = configuration)
        val source = ScriptSource.literal(
            sample.content,
            FluxonLang,
            sample.path.substringAfterLast('/').replace(Regex("[^A-Za-z0-9_]"), "_"),
        )
        val script = executor.compile(source, environment)
        return FluxonPreparedScript(script, environment)
    }

    override fun execute(prepared: FluxonPreparedScript): Any? {
        return prepared.script.eval(prepared.environment)
    }

}

private data class FluxonPreparedScript(
    val script: ModuleCompiledScript,
    val environment: ScriptEnvironment,
)

private fun renderCompileReport(results: List<CompileBenchmarkResult>, settings: BenchmarkSettings): String {
    return buildString {
        appendLine("=== module-script 编译/预处理性能测试 ===")
        appendLine("workload=fixed in samples, buildWarmup=${settings.buildWarmupIterations}, buildMeasure=${settings.buildMeasuredIterations}")
        appendLine("固定样本规模：limit=$FIXED_LIMIT, collection=$FIXED_COLLECTION_SIZE, string=$FIXED_STRING_SIZE")
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
        appendLine("固定样本规模：limit=$FIXED_LIMIT, collection=$FIXED_COLLECTION_SIZE, string=$FIXED_STRING_SIZE")
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
        appendLine("固定样本规模：limit=$FIXED_LIMIT, collection=$FIXED_COLLECTION_SIZE, string=$FIXED_STRING_SIZE")
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

private fun engineSamples(
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
