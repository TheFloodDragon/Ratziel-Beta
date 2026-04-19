import org.gradle.api.tasks.testing.Test
import org.gradle.process.JavaForkOptions

/**
 * 将 JFR 附着到所有 Test 与 JMH 任务的 fork JVM。
 * 每个任务生成独立的 `.jfr` 文件到 `outs/jfr/`，避免多 fork 覆盖。
 */
gradle.beforeProject {
    val sanitizedProjectPath = project.path.trim(':').replace(':', '-').ifEmpty { "root" }
    val jfrDir = rootProject.layout.projectDirectory.dir("outs/jfr").asFile

    fun attach(task: org.gradle.api.Task, phase: String) {
        val jfrFile = jfrDir.resolve("$sanitizedProjectPath-${task.name}-$phase.jfr")
        task.doFirst { jfrFile.parentFile.mkdirs() }
        (task as JavaForkOptions).jvmArgs(
            "-XX:StartFlightRecording=filename=${jfrFile.absolutePath},settings=profile,dumponexit=true"
        )
    }

    tasks.withType<Test>().configureEach { attach(this, "test") }

    // me.champeau.jmh 插件存在时挂到 `jmh` 任务上（JavaExec 类型，会 fork JVM）
    pluginManager.withPlugin("me.champeau.jmh") {
        tasks.matching { it.name == "jmh" }.configureEach { attach(this, "jmh") }
    }
}
