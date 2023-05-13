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

    initSubProject {
        publishing { createPublish(project) }
    }
}

buildDirClean()