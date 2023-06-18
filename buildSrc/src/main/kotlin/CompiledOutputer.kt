import org.gradle.api.Project
import java.io.File

const val outputFolder = "outs"

//输出编译后的文件到./outs
fun Project.output() {
    gradle.buildFinished {
        rootProject
            .childProjects["plugin"]!!.childProjects.values
            .forEach { it.outCopy(rootName) }
    }

    //    rootProject TODO do after 8000 years
    //        .childProjects["module"]!!.childProjects.values
    //        .forEach { copyByProject(it) }
}

/**
 * 复制项目输出文件到指定目录
 */
fun Project.outCopy(source: Array<out File>? = this.catchOut(), outDir: File = File(rootDir, outputFolder)) {
    outDir.mkdirs().takeIf { !outDir.exists() } //先创建文件
    //复制
    source?.forEach {
        it.copyTo(File(outDir, it.name), true)
    }
}

fun Project.outCopy(name: String, outDir: File = File(rootDir, outputFolder)) {
    this.outCopy(catchOut("${name}-${this.version}.jar"), outDir)
}

/**
 * 根据文件名找到输出文件
 */
fun Project.catchOut(name: String = "${this.name}-${this.version}.jar"): Array<out File>? {
    return File(this.buildDir, "libs").listFiles { file ->
        file.name == name
    }
}