import io.izzel.taboolib.gradle.BUKKIT_ALL
import io.izzel.taboolib.gradle.EXPANSION_PLAYER_FAKE_OP
import io.izzel.taboolib.gradle.KETHER

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
        install(BUKKIT_ALL, KETHER, EXPANSION_PLAYER_FAKE_OP, "nms", "nms-util-stable", "script-javascript")
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