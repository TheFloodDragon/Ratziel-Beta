import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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

    group = rootGroup
    version = rootVersion

    //TODO 八千年后再干
    if (parent?.name != "plugin" /*&& parent?.name != "module"*/) {
        buildDirClean()
    }

    tasks {
        withType<JavaCompile> { options.encoding = "UTF-8" }
        //一般配置
        withType<ShadowJar> {
            // Options
            archiveAppendix.set("")
            archiveClassifier.set("")
            archiveVersion.set(rootVersion)
            //archiveBaseName.set("$rootName-Bukkit")
            // Exclude
            exclude("META-INF/**")
            exclude("com/**", "org/**")
            // Adventure (不需要,因为是动态加载)
            //relocate("net.kyori", "$rootGroup.common.adventure")
            // Taboolib
            relocate("taboolib", "$rootGroup.taboolib")
            relocate("tb", "$rootGroup.taboolib")
            relocate("org.tabooproject", "$rootGroup.taboolib.library")
            // Kotlin
            relocate("kotlin.", "kotlin1820.") { exclude("kotlin.Metadata") }
            relocate("kotlinx.serialization", "kotlinx150.serialization")
        }
    }

}

buildDirClean()

output()