dependencies {
    // Core / Common
    shadowModule("module-core")
    shadowModule("module-common")
    // Platform - Bukkit
    shadowModule("runtime-bukkit")
    // Script
    shadowModule("module-script")
    // Compat
    tabooModule("module-compat-inject")
    shadowModule("module-compat-core")
    shadowModule("module-compat-bukkit")
    // Extension - Item
    shadowModule("module-item")
}

taboolib {
    env {
        // Platform - Bukkit
        install("platform-bukkit", "platform-bukkit-impl")
        // Bukkit - Basic
        install("bukkit-hook", "bukkit-util", "bukkit-fake-op", "bukkit-xseries")
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

tasks {
    shadowJar {
        dependencies {
            exclude(project(":project:module-compat-inject"))
        }
        archiveFileName.set("$rootName-Bukkit-$rootVersion.jar")
    }
    jar { dependsOn(project(":project:module-compat-inject").tasks.jar) }
}