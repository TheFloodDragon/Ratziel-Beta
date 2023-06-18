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

    //TODO 八千年后再干
    if (parent?.name != "plugin" /*&& parent?.name != "module"*/) {
        buildDirClean()
    }

}

buildDirClean()

output()