// 平台运行模块名称
val runtime: String by extra

subprojects {
    tasks.jar {
        // 添加通用语言
        doLast { // 编译完成后,打包前
            val langPath = "src/main/resources/lang" // 语言文件所在文件夹目录
            project(":project:module-common") // 通用语言文件所在模块
                .file(langPath)
                .listFiles().forEach { merger ->
                    // 获取Runtime模块
                    val runtimes: List<Project> =
                        if (project.hasProperty("runtime"))
                            listOf(project(":project:${project.property("runtime") as String}"))
                        else rootProject.allprojects.filter { p -> p.name.contains("runtime") }
                    runtimes.forEach {
                        it.file(langPath).listFiles() //获取该Runtime下的所有语言文件
                            .forEach { merged ->
                                if (merged.name == merger.name) { // 匹配相同语言的文本
                                    val out = File(project.buildDir, "cache/lang/${merged.name}")
                                    // 合并文件
                                    mergeYaml(merger, merged, out)
                                    // 加入打包文件中
                                    from(out) {
                                        into("resources/lang")
                                    }
                                }
                            }
                    }
                }
        }
    }
}

buildDirClean()