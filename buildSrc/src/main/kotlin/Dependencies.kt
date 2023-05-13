import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.PluginAware
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.*

fun PluginAware.applyPlugins() {
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.jetbrains.kotlin.jvm")
}

fun Project.buildDirClean() {
    @Suppress("DEPRECATION")
    gradle.buildFinished { buildDir.deleteRecursively() }
}

fun Project.initSubProject(publish: Project.() -> Unit) {
    group = rootGroup
    version = rootVersion

    tasks.withType<JavaCompile> { options.encoding = "UTF-8" }
    @Suppress("DEPRECATION")
    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    if (parent?.name != "plugin") {
        buildDirClean()
    }
    if (project.parent?.name == "project" || project.name == "project") {
        publish()
    }
}

fun RepositoryHandler.projectRepositories() {
    maven(repoTabooProject) {
        setAllowInsecureProtocol(true)
    }
    mavenCentral()
}

fun DependencyHandler.`framework`() {
    compileModule("framework-common", "framework-bukkit")
}

fun DependencyHandler.`adventure`() {
    usedAdventureModules.forEach {
        add("implementation", it)
    }
}

fun DependencyHandler.`serialization`() {
    add("compileOnly", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC")
}

fun DependencyHandler.`compileLocal`(project: Project, vararg dir: String) {
    dir.forEach { add("compileOnly", project.fileTree(it)) }
}

fun DependencyHandler.`compileModule`(vararg name: String) {
    name.forEach { add("compileOnly", project(":project:$it")) }
}

fun DependencyHandler.`implementateModule`(vararg name: String) {
    name.forEach { add("implementation", project(":project:$it")) }
}

fun DependencyHandler.`compileNMS`() {
    add("compileOnly", "ink.ptms:nms-all:1.0.0")
}

fun DependencyHandler.`compileCore`(
    version: Int,
    minimize: Boolean = true,
    mapped: Boolean = false,
    complete: Boolean = false,
) {
    val notation =
        "ink.ptms.core:v$version:$version${if (!complete && minimize) "-minimize" else ""}${if (complete) "" else if (mapped) ":mapped" else ":universal"}"
    add("compileOnly", notation)
}

fun DependencyHandler.compileTabooLib() {
    usedTaboolibModules.forEach { installTaboo(it) }
}

fun DependencyHandler.installTaboo(vararg module: String, version: String = taboolibVersion) = module.forEach {
    add("compileOnly", "io.izzel.taboolib:$it:$version")
}

fun DependencyHandler.shadowTaboo(vararg module: String, version: String = taboolibVersion) = module.forEach {
    add("implementation", "io.izzel.taboolib:$it:$version")
}