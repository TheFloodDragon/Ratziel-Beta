import io.izzel.taboolib.gradle.*
dependencies {
    // Core / Common
    shadowModule("module-core")
    shadowModule("module-common")
    // Platform - Bukkit
    shadowModule("runtime-bukkit")
    // Compat
    tabooModule("module-compat-inject")
    shadowModule("module-compat-core")
    shadowModule("module-compat-bukkit")
    // Extension - Item
    shadowModule("module-item")
}

taboolib {
    env {
        install(BUKKIT_ALL, KETHER, NMS_UTIL, EXPANSION_PLAYER_FAKE_OP)
    }
    description {
        dependencies {
            name("PlaceholderAPI").optional(true)
        }
    }
}

tasks.shadowJar {
    archiveFileName.set("$rootName-Bukkit-$rootVersion.jar")
}