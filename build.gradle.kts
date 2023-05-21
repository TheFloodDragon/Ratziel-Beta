plugins {
    java
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
    id("com.github.johnrengelman.shadow") version shadowJarVersion apply false
}

subprojects {
    applyPlugins()

    repositories {
        projectRepositories()
    }

    dependencies {
        compileOnly(kotlin("stdlib"))

        if (parent?.name == "plugin" || parent?.name == "project") {
            compileCore(11903)
            compileTabooLib()
            //MiniMessage: https://docs.adventure.kyori.net/minimessage/api.html
            adventure()
        }
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<JavaCompile> { options.encoding = "UTF-8" }

    group = rootGroup
    version = rootVersion

    if (parent?.name != "plugin" && parent?.name != "module") {
        buildDirClean()
    }

}

buildDirClean()

gradle.buildFinished {
    rootProject
        .childProjects["plugin"]!!.childProjects.values
        .forEach { copyByProject(it, "${rootName}-${it.version}") }

    rootProject
        .childProjects["module"]!!.childProjects.values
        .forEach { copyByProject(it) }
}

fun copyByProject(p: Project, caught: String = "${p.name}-${p.version}") {
    val outDir = File(rootDir, "outs")
    outDir.mkdirs().takeIf { !outDir.exists() }

    File(p.buildDir, "libs").listFiles { file ->
        file.name == "$caught.jar"
    }?.forEach {
        it.copyTo(File(outDir, it.name), true)
    }
}