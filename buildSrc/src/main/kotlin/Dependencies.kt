import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.project

fun PluginAware.applyPlugins() {
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.jetbrains.kotlin.jvm")
}

fun Project.buildDirClean() {
    @Suppress("DEPRECATION") gradle.buildFinished { buildDir.deleteRecursively() }
}

/**
 * 项目通用储存库
 */
fun RepositoryHandler.projectRepositories() {
    maven(repoTabooProject) {
        isAllowInsecureProtocol = true
    }
    maven("https://papermc.io/repo/repository/maven-public/")
    mavenCentral()
}

/**
 * KotlinJson序列化工具
 */
fun DependencyHandler.serialization() {
    add("compileOnly", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}

/**
 * NMS依赖
 */
fun DependencyHandler.compileNMS() {
    add("compileOnly", "ink.ptms:nms-all:1.0.0")
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
    add("compileOnly", notation)
}

/**
 * Adventure依赖
 */
fun DependencyHandler.adventure() {
    adventureModules.forEach { add("compileOnly", it) }
}

/**
 * Taboolib通用依赖
 */
fun DependencyHandler.compileTabooLib() {
    taboolibModules.forEach { installTaboo(it) }
}

/**
 * 依赖项目——仅编译时
 * @param name 项目名称
 */
fun DependencyHandler.compileModule(name: String) {
    add("compileOnly", project(":project:$name"))
}

fun DependencyHandler.installModule(name: String) {
    add("implementation", project(":project:$name"))
}

/**
 * 依赖所有项目——仅编译时
 */
fun DependencyHandler.compileAll() {
    project(":project").dependencyProject.childProjects.forEach { add("compileOnly", it.value) }
}

/**
 * 依赖Taboolib模块——仅编译时
 * @param module Taboolib模块名称
 * @param version Taboolib模块版本，默认为Taboolib版本
 */
fun DependencyHandler.installTaboo(vararg module: String, version: String = taboolibVersion) = module.forEach {
    add("compileOnly", "io.izzel.taboolib:$it:$version")
}

/**
 * 依赖Taboolib模块
 */
fun DependencyHandler.shadowTaboo(vararg module: String, version: String = taboolibVersion) = module.forEach {
    add("implementation", "io.izzel.taboolib:$it:$version")
}