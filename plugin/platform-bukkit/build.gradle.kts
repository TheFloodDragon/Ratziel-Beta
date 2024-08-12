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
        install("bukkit-hook", "bukkit-util", "bukkit-fake-op")
        // Bukkit - XSeries
        install("bukkit-xseries", "bukkit-xseries-item", "bukkit-xseries-skull")
        // NMS
//        install("nms", "nms-util-stable") TODO 6.2
        install("nms")
        // Script
//        install("minecraft-kether") TODO 6.2
    }
    description {
        dependencies {
            name("PlaceholderAPI").with("bukkit").optional(true)
        }
    }
}

tasks.shadowJar {
    archiveFileName.set("$rootName-Bukkit-$rootVersion.jar")
}