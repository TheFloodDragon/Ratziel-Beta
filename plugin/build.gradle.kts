import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

subprojects {

    dependencies {
        //通用模块
        shadowModule("module-core")
        shadowModule("module-common")
    }

    tasks {
        build {
            dependsOn(shadowJar)
        }
        withType<ShadowJar> {
            append("lang/zh_CN")
//            // 删除原有的
//            exclude("lang/*")
//            // 加入合并后的语言文件
//            from(outDir) {
//                into("lang")
//            }
        }
//        // Yaml合并
//        jar {
//            doFirst {
//                // 语言文件所在文件夹目录
//                val langPath = "src/main/resources/lang"
//                // 通用语言文件夹
//                val commonFiles = project(":project:module-common").file(langPath).listFiles()
//                // 获取Runtime模块
//                val rts: List<Project> = runtimes.map { project(":project:${it}") }
//                // 准备合并文件
//                commonFiles?.forEach { common ->
//                    rts.forEach {
//                        it.file(langPath).listFiles()
//                            ?.find { f -> f.name == common.name } // 匹配相同语言
//                            ?.let { merged ->
//                                mergeYaml( // 合并文件
//                                    common, merged, File(outDir, merged.name)
//                                )
//                            }
//                    }
//                }
//            }
//        }
    }
}

buildDirClean()