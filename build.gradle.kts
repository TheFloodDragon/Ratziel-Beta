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
        // Kether API From GitHub
        maven("https://maven.pkg.github.com/TheFloodDragon/Kether-API") {
            credentials {
                username = project.findProperty("githubUser") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("githubKey") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
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
            // MiniMessage - https://docs.adventure.kyori.net/minimessage/api.html
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

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    group = rootGroup
    version = rootVersion

    buildDirClean()

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
            exclude("exclude/**")
            // Taboolib
            relocate("tb.", "$rootGroup.taboolib.")
            relocate("taboolib.", "$rootGroup.taboolib.")
            relocate("org.tabooproject.", "$rootGroup.taboolib.library.")
            // Kotlin
            relocate("kotlin.", "kotlin${kotlinVersion.escapedVersion}.") { exclude("kotlin.Metadata") }
            relocate("kotlinx.", "kotlinx${kotlinVersion.escapedVersion}.")
        }
    }

}

buildDirClean()