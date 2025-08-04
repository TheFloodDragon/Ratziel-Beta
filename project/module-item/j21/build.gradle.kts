import org.jetbrains.kotlin.gradle.dsl.JvmTarget

repositories {
    maven("https://libraries.minecraft.net/")
}

dependencies {
    compileOnly(libs.altawk.nbt) { isTransitive = false }
    compileOnly(libs.taboolib.platform.bukkit)
    compileOnly(libs.taboolib.platform.bukkit.impl)
    compileOnly(libs.taboolib.bukkit.nms)
    compileOnly(libs.taboolib.bukkit.util)
    compileOnly(projects.project.runtimeBukkit)
    compileOnly(projects.project.moduleItem)
    compileOnly("com.mojang:datafixerupper:8.0.16")
    compileOnly("com.google.guava:guava:32.1.2-jre")
    compileCore(12105, mapped = true)
    compileOnly(fileTree("libs"))
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}