import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

const val outputFolder = "outs"

//输出编译后的文件到./outs
fun Project.output() {
    gradle.buildFinished {
        val outDir = File(rootDir, outputFolder)
        rootProject
            .childProjects["plugin"]!!.childProjects.values
            .forEach {
                val platformStr =
                    if (it.name.equals("all")) "" else "-${it.name.split('-')[1].capitalized()}"
                it.outCopy(
                    File(outDir, "$rootName$platformStr-${it.version}.jar")
                )
            }
    }

    //    rootProject TODO do after 8000 years
    //        .childProjects["module"]!!.childProjects.values
    //        .forEach { copyByProject(it) }
}

/**
 * 复制项目输出文件到指定目录
 */
fun Project.outCopy(
    target: File,//输出目标
    source: File? = this.catchOut()//源
) {
    //outDir.mkdirs().takeIf { !outDir.exists() } //先创建文件
    //复制
    source?.copyTo(target, true)
}

/**
 * 根据文件名找到输出文件
 */
fun Project.catchOut(name: String = "${this.name}-${this.version}.jar"): File? {
    return File(this.buildDir, "libs").listFiles { file ->
        file.name == name
    }?.first()
}