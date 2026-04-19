import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.testing.Test
import org.gradle.process.JavaForkOptions

/**
 * 将 JFR 附着到所有 Test 与 JMH 任务的 fork JVM，生成 `.jfr` 到 `outs/jfr/`。
 *
 * Test 任务通过 `JavaForkOptions.jvmArgs` 直接追加；
 * JMH 任务走 `me.champeau.jmh` 插件扩展的 `jvmArgsAppend` 属性。
 */
gradle.beforeProject {
    val sanitizedProjectPath = project.path.trim(':').replace(':', '-').ifEmpty { "root" }
    val jfrDir = rootProject.layout.projectDirectory.dir("outs/jfr").asFile

    fun jfrArg(taskName: String, phase: String): String {
        val jfrFile = jfrDir.resolve("$sanitizedProjectPath-$taskName-$phase.jfr")
        jfrFile.parentFile.mkdirs()
        return "-XX:StartFlightRecording=filename=${jfrFile.absolutePath},settings=profile,dumponexit=true"
    }

    tasks.withType<Test>().configureEach {
        (this as JavaForkOptions).jvmArgs(jfrArg(name, "test"))
    }

    pluginManager.withPlugin("me.champeau.jmh") {
        val jmhExt = extensions.getByName("jmh")
        @Suppress("UNCHECKED_CAST")
        val jvmArgsAppend = jmhExt.javaClass.getMethod("getJvmArgsAppend")
            .invoke(jmhExt) as ListProperty<String>
        jvmArgsAppend.add(jfrArg("jmh", "jmh"))
    }
}
