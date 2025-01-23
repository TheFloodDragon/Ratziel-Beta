import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.tasks.JvmConstants

fun Project.buildDirClean() {
    @Suppress("DEPRECATION") gradle.buildFinished { buildDir.deleteRecursively() }
}

/**
 * NMS依赖
 */
fun DependencyHandler.compileNMS() {
    add(JvmConstants.COMPILE_ONLY_CONFIGURATION_NAME, "ink.ptms:nms-all:1.0.0")
}

/**
 * 核心依赖
 * @param version 核心版本号
 */
fun DependencyHandler.compileCore(
    version: Int,
    minimize: Boolean = false,
    mapped: Boolean = false,
    complete: Boolean = false,
) {
    val notation =
        "ink.ptms.core:v$version:$version${if (!complete && minimize) "-minimize" else ""}${if (complete) "" else if (mapped) ":mapped" else ":universal"}"
    add(JvmConstants.COMPILE_ONLY_CONFIGURATION_NAME, notation)
}