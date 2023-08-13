import org.gradle.internal.impldep.org.yaml.snakeyaml.DumperOptions
import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml
import java.io.FileInputStream
import java.io.FileWriter

// 平台运行模块名称
val runtime: String by extra

dependencies { compileOnly("org.yaml:snakeyaml:2.1") }

subprojects {

    dependencies { compileOnly("org.yaml:snakeyaml:2.1") }

    tasks.jar {
        // 添加通用语言
        doLast { // 编译完成后,打包前
            val langPath = "src/main/resources/lang" // 语言文件所在文件夹目录
            project(":project:module-common") // 通用语言文件所在模块
                .file(langPath)
                .listFiles().forEach { merger ->
                    //println(project.file(langPath).listFiles())
                    // 获取Runtime模块
                    val runtimes: List<Project> =
                        if (project.hasProperty("runtime"))
                            listOf(project(":project:${project.property("runtime") as String}"))
                        else rootProject.allprojects.filter { p -> p.name.contains("runtime") }
                    runtimes.forEach {
                        it.file(langPath).listFiles() //获取该Runtime下的所有语言文件
                            .forEach { merged ->
                                val out = File(project.buildDir, "cache/lang/${merged.name}")
                                println(out)
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

/**
 * 合并两个YAML文件
 * 若有冲突则保留被合并的
 * @param merger 合并者
 * @param merged 被合并的
 */
fun mergeYaml(merger: File, merged: File, out: File) {
    val options = DumperOptions()
    options.isPrettyFlow = true
    options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
    val yaml = Yaml(options)

    val data1 = yaml.loadAs(FileInputStream(merger), Map::class.java)
    val data2 = yaml.loadAs(FileInputStream(merged), Map::class.java)
    // data2优先级高于data1
    val mergedData = data1.toMutableMap().putAll(data2)

    FileWriter(out).use {
        yaml.dump(mergedData, it)
    }
}

buildDirClean()