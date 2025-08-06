import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.shadow)
}

subprojects {

    // 应用插件
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.gradleup.shadow")

    repositories {
        // 中央库
        mavenCentral()
        // Taboolib 仓库
        maven("https://repo.tabooproject.org/repository/releases")
        // PaperMC
        maven("https://papermc.io/repo/repository/maven-public/")
        // JitPack
        maven("https://jitpack.io")
    }

    dependencies {
        // Kotlin标准库
        compileOnly(kotlin("stdlib"))

        // 项目依赖
        if (path.contains("project")) {
            // Kotlin 序列化工具
            compileOnly(rootProject.libs.kotlinx.serialization.json)
            // Kotlin 协程工具
            compileOnly(rootProject.libs.kotlinx.coroutines.core)
            // Adventure API
            compileOnly(rootProject.libs.bundles.adventure)
            // Taboolib 通用模块
            compileOnly(rootProject.libs.bundles.taboolib)
            // 基本依赖
            if (name != rootProject.projects.project.moduleCore.name) {
                compileOnly(rootProject.projects.project.moduleCore)
                if (name != rootProject.projects.project.moduleCommon.name) {
                    compileOnly(rootProject.projects.project.moduleCommon)
                }
            }
        }

    }

    // Java 构建设置
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // 编码设置
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    // Kotlin 构建设置
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
            freeCompilerArgs.addAll("-Xallow-unstable-dependencies")
        }
    }

    // ShadowJar 基础配置
    tasks.shadowJar {
        // Options
        archiveAppendix.set("")
        archiveClassifier.set("")
        archiveVersion.set(version.toString())
        destinationDirectory.set(file("$rootDir/outs"))
        // Taboolib
        relocate("taboolib", "${rootProject.group}.taboolib")
        // NBT
        relocate("cn.altawk.nbt.", "${rootProject.group}.module.nbt.")
        // Kotlin
//        relocate("kotlin.", "kotlin${kotlinVersion.escapedVersion}.") { exclude(skipRelocateKotlinClasses) }
//        relocate("kotlinx.coroutines.", "kotlinx${kotlinVersion.escapedVersion}.coroutines${coroutineVersion.escapedVersion}.")
//        relocate("kotlinx.serialization.", "kotlinx${kotlinVersion.escapedVersion}.serialization${serializationVersion.escapedVersion}.")
    }

    buildDirClean()

}

buildDirClean()