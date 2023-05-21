
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
    @Suppress("DEPRECATION")
    gradle.buildFinished { buildDir.deleteRecursively() }
}

fun RepositoryHandler.projectRepositories() {
    maven(repoTabooProject) {
        isAllowInsecureProtocol = true
    }
    mavenCentral()
}

fun DependencyHandler.compileNMS() {
    add("compileOnly", "ink.ptms:nms-all:1.0.0")
}

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


fun DependencyHandler.adventure() {
    adventureModules.forEach {
        add("compileOnly", it)
    }
}

fun DependencyHandler.compileTabooLib() {
    taboolibModules.forEach { installTaboo(it) }
}

fun DependencyHandler.compileModule(vararg name: String) {
    name.forEach { add("compileOnly", project(":project:$it")) }
}

fun DependencyHandler.implementModule(vararg name: String) {
    name.forEach { add("implementation", project(":project:$it")) }
}

fun DependencyHandler.installTaboo(vararg module: String, version: String = taboolibVersion) = module.forEach {
    add("compileOnly", "io.izzel.taboolib:$it:$version")
}

fun DependencyHandler.shadowTaboo(vararg module: String, version: String = taboolibVersion) = module.forEach {
    add("implementation", "io.izzel.taboolib:$it:$version")
}