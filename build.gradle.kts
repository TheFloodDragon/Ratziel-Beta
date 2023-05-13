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
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    initSubProject()
}

buildDirClean()

gradle.buildFinished {
    project.allprojects.forEach {
        if (it.name.equals("plugin")) copyByProject(it, rootName)
        else if (it.name.equals("module")) it.childProjects.values.forEach { p -> copyByProject(p) }
    }
}

fun copyByProject(p: Project, fn: String = p.name) {
    val outDir = File(rootDir, "outs")
    outDir.mkdirs().takeIf { !outDir.exists() }

    File(p.buildDir, "libs").listFiles { file ->
        file.name == "$fn-${p.version}.jar"
    }?.forEach {
        it.copyTo(File(outDir, it.name), true)
    }
}