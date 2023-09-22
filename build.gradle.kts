import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
    id("com.github.johnrengelman.shadow") version shadowJarVersion apply false
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion apply false
}

subprojects {

    applyPlugins()

    repositories {
        mavenCentral()
        // 坏黑私人库
        maven("http://ptms.ink:8081/repository/releases") {
            isAllowInsecureProtocol = true
        }
        // PaperMC
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    dependencies {
        compileOnly(kotlin("stdlib"))

        // 项目一般依赖
        if (parent?.name == "project") {
            compileCore(12001)
            compileTabooLib()
            // MiniMessage - https://docs.adventure.kyori.net/minimessage/api.html
            adventure()
            // Kotlin序列化工具
            serialization()
            // Kotlin协程工具
            coroutine()
        }

        // 模块一般依赖——所有
        if (parent?.name == "script") {
            compileAll()
        }

        // Runtime默认模块依赖
        if (name.contains("runtime")) {
            installModule("module-core")
            installModule("module-common")
        }
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    group = rootGroup
    version = rootVersion

    if (parent?.name != "plugin" && parent?.name != "script") {
        buildDirClean()
    }

    tasks {
        withType<JavaCompile> { options.encoding = "UTF-8" } //UTF-8 编码
        //一般配置
        withType<ShadowJar> {
            // Options
            archiveAppendix.set("")
            archiveClassifier.set("")
            archiveVersion.set(rootVersion)
            destinationDirectory.set(file("$rootDir/outs")) //输出路径
            // Exclude
            exclude("META-INF/**")
            exclude("com/**", "org/**")
            // Taboolib
            relocate("taboolib", "$rootGroup.taboolib")
            relocate("tb", "$rootGroup.taboolib")
            relocate("org.tabooproject", "$rootGroup.taboolib.library")
            // Kotlin
            relocate("kotlin.", "kotlin1910.") { exclude("kotlin.Metadata") }
            relocate("kotlinx.", "kotlinx1910.")
        }
    }

}

buildDirClean()