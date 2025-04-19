dependencies {
    // Core / Common
    implementation(projects.project.moduleCore)
    implementation(projects.project.moduleCommon)
    // Platform - Bukkit
    implementation(projects.project.runtimeBukkit)
    // Script
    implementation(projects.project.moduleScript)
    // Compat
    implementation(projects.project.moduleCompatCore)
    implementation(projects.project.moduleCompatInject)
    // Extension - Item
    implementation(projects.project.moduleItem)
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