import groovy.json.JsonSlurper
import java.util.Locale

plugins {
    id("me.champeau.jmh") version "0.7.3"
}

dependencies {
    // Kotlin Scripting
    compileOnly(kotlin("scripting-common"))
    compileOnly(kotlin("scripting-jvm"))
    compileOnly(kotlin("scripting-jvm-host"))
    compileOnly(kotlin("scripting-compiler-embeddable"))
    compileOnly(kotlin("compiler-embeddable"))
    // Fluxon
    implementation(libs.fluxon) { isTransitive = false }
    // JavaScript: Nashorn Engine
    compileOnly(libs.nashorn)
    // JavaScript: GraalJs
    compileOnly(libs.graalvm.polyglot)
    // Jexl3: Apache
    compileOnly(libs.jexl)

    // JMH 源集：me.champeau.jmh 不自动从 testImplementation 继承，这里显式复制
    "jmhImplementation"(kotlin("stdlib"))
    "jmhImplementation"(kotlin("reflect"))
    "jmhImplementation"(kotlin("scripting-common"))
    "jmhImplementation"(kotlin("scripting-jvm"))
    "jmhImplementation"(kotlin("scripting-jvm-host"))
    "jmhImplementation"(kotlin("scripting-compiler-embeddable"))
    "jmhImplementation"(kotlin("compiler-embeddable"))
    "jmhRuntimeOnly"("org.jetbrains.kotlin:kotlin-script-runtime:${rootProject.libs.versions.kotlin.get()}")
    "jmhRuntimeOnly"("org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:${rootProject.libs.versions.kotlin.get()}")
    "jmhImplementation"(libs.fluxon) { isTransitive = false }
    "jmhImplementation"(libs.nashorn)
    "jmhImplementation"(libs.graalvm.polyglot)
    "jmhRuntimeOnly"("org.graalvm.truffle:truffle-runtime:${rootProject.libs.versions.graaljs.get()}")
    "jmhRuntimeOnly"("org.graalvm.js:js-language:${rootProject.libs.versions.graaljs.get()}")
    "jmhImplementation"(libs.jexl)
    "jmhImplementation"(rootProject.libs.bundles.taboolib)
}

// 资源处理
tasks.processResources {
    filesMatching("**/*.json") {
        expand(
            "kotlinVersion" to rootProject.libs.versions.kotlin.get(),
            "nashornVersion" to rootProject.libs.versions.nashorn.get(),
            "graaljsVersion" to rootProject.libs.versions.graaljs.get(),
            "jexlVersion" to rootProject.libs.versions.jexl.get(),
        )
    }
}

// JMH 基准配置
jmh {
    fork.set(1)
    warmupIterations.set(5)
    warmupForks.set(0)
    iterations.set(10)
    warmup.set("1s")
    timeOnIteration.set("1s")
    timeUnit.set("us")
    benchmarkMode.set(listOf("avgt"))
    resultFormat.set("JSON")
    resultsFile.set(layout.buildDirectory.file("reports/jmh/results.json"))
    humanOutputFile.set(layout.buildDirectory.file("reports/jmh/human.txt"))
    jvmArgs.set(listOf("-Xmx2g"))
    includeTests.set(false)
    failOnError.set(false)
}

/** 渲染 JMH 报告（avg/best/worst/stdev/p50/p99，按 scriptCase 分组、按 avg 排名）。 */
val renderJmhReport by tasks.registering {
    group = "benchmark"
    description = "Render JMH JSON result as a Markdown report under outs/jmh/report.md"
    val resultsFile = layout.buildDirectory.file("reports/jmh/results.json")
    val outputDir = rootProject.layout.projectDirectory.dir("outs/jmh").asFile
    inputs.file(resultsFile).withPropertyName("jmhResults")
    outputs.dir(outputDir).withPropertyName("reportDir")
    doLast {
        val file = resultsFile.get().asFile
        if (!file.exists()) {
            logger.warn("[renderJmhReport] JMH results not found at ${file.absolutePath}, skip")
            return@doLast
        }
        outputDir.mkdirs()
        val report = renderJmhReport(file)
        val target = outputDir.resolve("report.md")
        target.writeText(report, Charsets.UTF_8)
        println(report)
        println("[renderJmhReport] wrote ${target.absolutePath}")
    }
}

tasks.named("jmh").configure { finalizedBy(renderJmhReport) }

/**
 * 解析 JMH JSON 并生成 Markdown 报告。
 *
 * `params.scriptCase` 由 `ScriptBenchmarkBase` 以 `"id|displayName"` 形式声明，这里按 `|` 拆开即得
 * 分组键与人类可读名；不需要任何外部映射表。
 */
@Suppress("UNCHECKED_CAST")
fun renderJmhReport(resultsJson: java.io.File): String {
    val raw = JsonSlurper().parse(resultsJson) as List<Map<String, Any?>>
    data class Entry(
        val phase: String,
        val engine: String,
        val scriptCaseId: String,
        val displayName: String,
        val unit: String,
        val score: Double,
        val stdev: Double,
        val p50: Double,
        val p99: Double,
        val best: Double,
        val worst: Double,
    )

    val entries = raw.map { node ->
        val benchmark = node["benchmark"] as String
        val phase = when {
            benchmark.contains("HotExecutionBenchmark") -> "Hot"
            benchmark.contains("ColdStartBenchmark") -> "Cold"
            benchmark.contains("CompilationBenchmark") -> "Compile"
            else -> "Other"
        }
        val params = (node["params"] as? Map<String, Any?>).orEmpty()
        val engine = params["engine"]?.toString() ?: "?"
        // ScriptBenchmarkBase 把 scriptCase 编码为 "id|displayName"；非 @Param 字段不会出现在 JMH JSON
        // 的 params 里，所以从这个唯一字段拆回两段。
        val scriptCaseRaw = params["scriptCase"]?.toString() ?: "?"
        val scriptCaseId = scriptCaseRaw.substringBefore('|')
        val displayName = scriptCaseRaw.substringAfter('|', scriptCaseId)
        val metric = node["primaryMetric"] as Map<String, Any?>
        val unit = metric["scoreUnit"]?.toString() ?: "?"
        val score = (metric["score"] as Number).toDouble()
        val stdev = (metric["scoreError"] as? Number)?.toDouble() ?: Double.NaN
        val percentiles = (metric["scorePercentiles"] as? Map<String, Any?>).orEmpty()
        val p50 = (percentiles["50.0"] as? Number)?.toDouble() ?: Double.NaN
        val p99 = (percentiles["99.0"] as? Number)?.toDouble() ?: Double.NaN
        val rawData = (metric["rawData"] as? List<List<Any?>>).orEmpty()
        val flat = rawData.flatten().mapNotNull { (it as? Number)?.toDouble() }
        val best = flat.minOrNull() ?: score
        val worst = flat.maxOrNull() ?: score
        Entry(phase, engine, scriptCaseId, displayName, unit, score, stdev, p50, p99, best, worst)
    }

    val sb = StringBuilder()
    sb.appendLine("# 脚本模块 JMH 基准报告")
    sb.appendLine()
    sb.appendLine("样本来源：`build/reports/jmh/results.json`；每行 **avg/best/worst** 单位在表右侧标注，stdev 为 JMH 误差估计（99.9% CI）。")
    sb.appendLine()
    listOf(
        "Compile" to "## 编译 / 预处理（SingleShotTime）",
        "Hot" to "## 热执行（AverageTime）",
        "Cold" to "## 冷启动（SingleShotTime）",
    ).forEach { (phase, title) ->
        sb.appendLine(title)
        sb.appendLine()
        val phaseEntries = entries.filter { it.phase == phase }
        if (phaseEntries.isEmpty()) {
            sb.appendLine("_无结果_")
            sb.appendLine()
            return@forEach
        }
        val byCase = phaseEntries.groupBy { it.scriptCaseId }.toSortedMap()
        byCase.forEach { (id, rows) ->
            val displayName = rows.first().displayName
            val label = if (displayName == id) id else "$displayName（`$id`）"
            sb.appendLine("### $label")
            sb.appendLine()
            sb.appendLine("| 排名 | 引擎 | avg | best | worst | stdev | p50 | p99 | 相对最快 | 单位 |")
            sb.appendLine("|---:|:---|---:|---:|---:|---:|---:|---:|---:|:---|")
            val sorted = rows.sortedBy { it.score }
            val fastest = sorted.firstOrNull()?.score ?: 1.0
            sorted.forEachIndexed { idx, row ->
                sb.appendLine(
                    String.format(
                        Locale.ROOT,
                        "| %d | %s | %.3f | %.3f | %.3f | %.3f | %.3f | %.3f | %.2fx | %s |",
                        idx + 1,
                        row.engine,
                        row.score,
                        row.best,
                        row.worst,
                        row.stdev,
                        row.p50,
                        row.p99,
                        if (fastest == 0.0) 1.0 else row.score / fastest,
                        row.unit,
                    )
                )
            }
            sb.appendLine()
        }
    }
    return sb.toString()
}
