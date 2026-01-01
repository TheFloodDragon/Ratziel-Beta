import org.jetbrains.kotlin.gradle.dsl.JvmTarget

repositories {
    maven("https://repo.momirealms.net/releases/")
    maven("https://repo.gtemc.net/releases/")
}

dependencies {
    compileOnly(projects.project.moduleCompatCore)
    compileOnly(projects.project.moduleCompatBukkit)
    compileOnly(projects.project.runtimeBukkit)
    compileOnly(projects.project.moduleItem)
    compileOnly("net.momirealms:craft-engine-core:0.0.60")
    compileOnly("net.momirealms:craft-engine-bukkit:0.0.60")
    implementation("cn.gtemc:itembridge:1.0.17")
    compileCore(12104)
    compileOnly(fileTree("libs"))
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}