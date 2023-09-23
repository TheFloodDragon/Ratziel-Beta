plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven("https://repo.codemc.io/repository/nms/")
}

dependencies { implementation("org.bukkit:bukkit:1.20-R0.1-SNAPSHOT") }

@Suppress("DEPRECATION") gradle.buildFinished { buildDir.deleteRecursively() }