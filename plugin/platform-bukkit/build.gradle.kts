dependencies {
    // Core / Common
    runtimeOnly(projects.project.moduleCore)
    runtimeOnly(projects.project.moduleCommon)
    // Platform - Bukkit
    runtimeOnly(projects.project.runtimeBukkit)
    // Script
    runtimeOnly(projects.project.moduleScript)
    // Compat
    runtimeOnly(projects.project.moduleCompatCore)
    runtimeOnly(projects.project.moduleCompatInject)
    runtimeOnly(projects.project.moduleCompatBukkit)
    // Module - Item
    runtimeOnly(projects.project.moduleItem)
}

taboolib {
    env {
        // Platform - Bukkit
        install("platform-bukkit", "platform-bukkit-impl")
        // Bukkit - Basic
        install("bukkit-hook", "bukkit-util", "bukkit-fake-op", "bukkit-xseries", "minecraft-metrics")
        // NMS
        install("bukkit-nms", "bukkit-nms-stable")
        // Script
        install("minecraft-kether")
    }
    description {
        dependencies {
            name("PlaceholderAPI").with("bukkit").optional(true)
        }
    }
}

tasks.shadowJar {
    from(zipTree(tasks.taboolibMainTask.get().inJar))
    archiveFileName.set("${rootProject.name}-Bukkit-$version.jar")
    destinationDirectory.set(file("$rootDir/build")) // 暂时不输出
}