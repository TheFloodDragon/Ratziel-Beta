import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version shadowJarVersion
}

subprojects {

    applyPlugins()

    repositories {
        // 中央库
        mavenCentral()
        // 坏黑私人库
        maven("http://ptms.ink:8081/repository/releases") { isAllowInsecureProtocol = true }
        // PaperMC
        maven("https://papermc.io/repo/repository/maven-public/")
        // OSS
        maven("https://s01.oss.sonatype.org/content/groups/public/")
        // PlaceholderAPI
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    dependencies {
        // Kotlin标准库
        compileOnly(kotlin("stdlib"))

        // 项目依赖
        if (parent?.name == "project") {
            // Taboolib通用模块
            compileTabooCommon()
            // Adventure API
            adventure()
            // Kotlin序列化工具
            serialization()
            // Kotlin协程工具
            coroutine()
            // 基本依赖
            arrayOf(
                "module-core",
                "module-common".takeIf { name != "module-core" }
            ).forEach { module -> module.takeIf { name != it }?.let { compileModule(it) } }
        }

    }

    // Java 构建设置
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Kotlin 构建设置
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

    // ShadowJar 基础配置
    tasks.shadowJar {
        // Options
        archiveAppendix.set("")
        archiveClassifier.set("")
        archiveVersion.set(rootVersion)
        destinationDirectory.set(file("$rootDir/outs"))
        // Taboolib
        relocate("taboolib", "$rootGroup.taboolib")
        // Kotlin
        relocate("kotlin.", "kotlin${kotlinVersion.escapedVersion}.") { exclude(skipRelocateKotlinClasses) }
        relocate("kotlinx.coroutines.", "kotlinx.coroutines${coroutineVersion.escapedVersion}.")
        relocate("kotlinx.serialization.", "kotlinx.serialization${serializationVersion.escapedVersion}.")
    }

    group = rootGroup
    version = rootVersion

}

buildDirClean()