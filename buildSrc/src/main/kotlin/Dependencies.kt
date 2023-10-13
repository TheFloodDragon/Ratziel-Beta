import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.project

fun PluginAware.applyPlugins() {
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
}

fun Project.buildDirClean() {
    @Suppress("DEPRECATION") gradle.buildFinished { buildDir.deleteRecursively() }
}

/**
 * Kotlin序列化工具
 */
fun DependencyHandler.serialization() {
    add(ACTION_COMPILE, "org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
}

/**
 * Kotlin协程工具
 */
fun DependencyHandler.coroutine() {
    add(ACTION_COMPILE, "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
}

/**
 * NMS依赖
 */
fun DependencyHandler.compileNMS() {
    add(ACTION_COMPILE, "ink.ptms:nms-all:1.0.0")
}

/**
 * 核心依赖
 * @param version 核心版本号
 */
fun DependencyHandler.compileCore(
    version: Int,
    minimize: Boolean = true,
    mapped: Boolean = false,
    complete: Boolean = false,
) {
    val notation =
        "ink.ptms.core:v$version:$version${if (!complete && minimize) "-minimize" else ""}${if (complete) "" else if (mapped) ":mapped" else ":universal"}"
    add(ACTION_COMPILE, notation)
}

/**
 * Adventure依赖
 */
fun DependencyHandler.adventure() {
    adventureModules.forEach { add(ACTION_COMPILE, it) }
}

/**
 * Taboolib通用依赖
 */
fun DependencyHandler.compileTabooCommon() {
    taboolibModules.forEach { compileTaboo(it) }
}

/**
 * 依赖项目
 * @param name 项目名称
 */
fun DependencyHandler.compileModule(name: String) {
    add(ACTION_COMPILE, project(":project:$name"))
}

fun DependencyHandler.installModule(name: String) {
    add(ACTION_INSTALL, project(":project:$name"))
}

fun DependencyHandler.shadowModule(name: String) {
    add(ACTION_SHADOW, project(":project:$name"))
}

/**
 * 依赖所有项目
 */
fun DependencyHandler.compileAll() {
    project(":project").dependencyProject.childProjects.forEach { add(ACTION_COMPILE, it.value) }
}

/**
 * 依赖Taboolib模块
 * @param module Taboolib模块名称
 * @param version Taboolib版本
 */
fun DependencyHandler.compileTaboo(vararg module: String, version: String = taboolibVersion) = module.forEach {
    add(ACTION_COMPILE, "io.izzel.taboolib:$it:$version")
}

fun DependencyHandler.shadowTaboo(vararg module: String, version: String = taboolibVersion) = module.forEach {
    add(ACTION_SHADOW, "io.izzel.taboolib:$it:$version")
}

private const val ACTION_COMPILE = "compileOnly"
private const val ACTION_INSTALL = "api"
private const val ACTION_SHADOW = "implementation"