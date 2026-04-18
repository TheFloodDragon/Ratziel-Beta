import org.gradle.api.tasks.testing.Test

gradle.beforeProject {
    tasks.withType<Test>().configureEach {
        val sanitizedProjectPath = project.path.trim(':').replace(':', '-').ifEmpty { "root" }
        val jfrDir = rootProject.layout.projectDirectory.dir("outs/jfr").asFile
        val jfrFile = jfrDir.resolve("${sanitizedProjectPath}-${name}.jfr")

        doFirst {
            jfrFile.parentFile.mkdirs()
        }

        jvmArgs(
            "-XX:StartFlightRecording=filename=${jfrFile.absolutePath},settings=profile,dumponexit=true"
        )
    }
}
