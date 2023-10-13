plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

dependencies {
    compileCore(12001)
    compileOnly("com.google.code.gson:gson:2.8.9")
    compileOnly("net.md-5:bungeecord-chat:1.17")
    compileOnly("me.clip:placeholderapi:2.11.4")
    // Module - Kether
    installModule("module-kether")
    // Module - Folia
    installModule("module-folia")
}

taboolib {
    version = taboolibVersion
    taboolibModules.forEach { install(it) }
    install("platform-bukkit")
    install("module-nms")
    install("module-nms-util")
    install("expansion-player-fake-op")

    description {
        name = rootName

        contributors {
            name("TheFloodDragon")
        }

        dependencies {
            name("PlaceholderAPI").optional(true)
        }

        bukkitNodes = HashMap<String, Any>().apply {
            // API Version
            put("api-version", 1.13)
            // Folia Support
            put("folia-supported", true)
            // Build Info
            put("built-date", currentISODate)
            put("built-by", systemUserName)
            put("built-os", systemOS)
            put("built-ip", systemIP)
        }

    }

    // 排除原有的防止重复 (为了支持Folia)
    exclude("taboolib/platform/BukkitPlugin")
    exclude("taboolib/platform/type/BukkitPlayer")

    classifier = null
    options("skip-minimize", "keep-kotlin-module", "skip-taboolib-relocate")
}