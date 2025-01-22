import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.kotlin
    id("com.gradleup.shadow") version shadowJarVersion
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
        // 坏黑私人库
        maven("http://sacredcraft.cn:8081/repository/releases") { isAllowInsecureProtocol = true }
        // PaperMC
        maven("https://papermc.io/repo/repository/maven-public/")
        // OSS
        maven("https://s01.oss.sonatype.org/content/groups/public/")
        // NBT (Github Packages)
        maven {
            url = uri("https://maven.pkg.github.com/TheFloodDragon/nbt")
            credentials {
                username = project.findProperty("github.user") as String? ?: System.getenv("GITHUB_USER")
                password = project.findProperty("github.token") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    dependencies {
        // Kotlin标准库
        compileOnly(kotlin("stdlib"))

        // 项目依赖
        if (parent?.name == "project") {
            // Kotlin序列化工具
            serialization()
            // Kotlin协程工具
            coroutine()
            // Adventure API
            adventure()
            // Taboolib通用模块
            taboolibModules.forEach { compileTaboo(it) }
            // 基本依赖
            if (name != "module-core") compileModule("module-core")
            if (name != "module-core" && name != "module-common") compileModule("module-common")
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
            freeCompilerArgs.add("-Xallow-unstable-dependencies")
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
//        relocate("kotlin.", "kotlin${kotlinVersion.escapedVersion}.") { exclude(skipRelocateKotlinClasses) }
//        relocate("kotlinx.coroutines.", "kotlinx${kotlinVersion.escapedVersion}.coroutines${coroutineVersion.escapedVersion}.")
//        relocate("kotlinx.serialization.", "kotlinx${kotlinVersion.escapedVersion}.serialization${serializationVersion.escapedVersion}.")
    }

    group = rootGroup
    version = rootVersion

}

buildDirClean()