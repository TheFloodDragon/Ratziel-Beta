import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

// 平台运行模块名称
val runtime: String by extra

subprojects {
    val outDir = File(project.buildDir, "cache/lang")

    tasks {
        build {
            dependsOn(shadowJar)
        }
        withType<ShadowJar> {
            // 加入打包的文件中
            from(outDir) {
                into("lang")
            }
        }
        // Yaml合并
        jar {
            doFirst { // 编译完成后,打包前
                val langPath = "src/main/resources/lang" // 语言文件所在文件夹目录
                // 通用语言文件所在模块
                val commonFiles = project(":project:module-common")
                    .file(langPath)
                    .listFiles()
                // 获取Runtime模块
                val runtimes: List<Project> =
                    if (project.hasProperty("runtime"))
                        listOf(project(":project:${project.property("runtime") as String}"))
                    else rootProject.allprojects.filter { p -> p.name.contains("runtime") }
                // 准备合并文件
                commonFiles.forEach { merger ->
                    runtimes.forEach {
                        it.file(langPath).listFiles() //获取该Runtime下的所有语言文件
                            .forEach { merged ->
                                if (merged.name == merger.name) { // 匹配相同语言的文本
                                    val out = File(outDir, merged.name)
                                    // 合并文件
                                    mergeYaml(merger, merged, out)
                                }
                            }
                    }
                }
            }
        }
    }
}

buildDirClean()