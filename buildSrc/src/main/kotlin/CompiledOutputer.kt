import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

const val outputFolder = "outs"

//输出编译后的文件到./outs
fun Project.output() {
    @Suppress("DEPRECATION") gradle.buildFinished {
        val outDir = File(rootDir, outputFolder)
        allprojects.forEach {
            if (it.parent?.name.equals("plugin")) {
                it.outCopy(File(outDir, nameOfPlugin(it)))
            } else if (it.parent?.name.equals("script")) {
                it.outCopy(File(outDir, nameOfScript(it)))
            }
        }
    }
}

fun nameOfPlugin(p: Project): String {
    return if (p.name.equals("all"))
        "$rootName-${p.version}.jar"
    else "$rootName-${p.name.split('-')[1].capitalized()}-${p.version}.jar"
}

fun nameOfScript(p: Project): String {
    return "Script-${p.name.capitalized()}-${p.version}.jar"
}

/**
 * 复制项目输出文件到指定目录
 */
fun Project.outCopy(
    target: File,
    source: File? = this.catchOut()
) {
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