import org.jetbrains.kotlin.gradle.dsl.JvmTarget

repositories {
    maven("https://repo.momirealms.net/releases/")
}

dependencies {
    compileOnly(projects.project.moduleCompatCore)
    compileOnly(projects.project.moduleCompatBukkit)
    compileOnly(projects.project.runtimeBukkit)
    compileOnly(projects.project.moduleItem)
    compileOnly("net.momirealms:craft-engine-core:0.0.60")
    compileOnly("net.momirealms:craft-engine-bukkit:0.0.60")
    compileCore(12104)
    compileOnly(fileTree("libs"))
}


kotlin {
    jvmToolchain(23)
}
