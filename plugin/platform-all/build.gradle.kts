// All Platforms
val platforms = arrayOf("platform-bukkit").map { project(":plugin:$it") }

dependencies {
    platforms.forEach { taboo(project(":plugin:${it.name}", "shadow")) }
}

taboolib {
    env {
        platforms.flatMap { it.taboolib.env.modules }.forEach { install(it) }
    }
}

tasks.shadowJar {
    archiveFileName.set("${rootProject.name}-$version.jar")
}