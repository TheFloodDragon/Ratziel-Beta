plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

dependencies {
    compileCore(12002)
    compileOnly("com.google.code.gson:gson:2.8.9")
    compileOnly("net.md-5:bungeecord-chat:1.17")
    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.4")
    // Folia
    taboo("com.tcoded:FoliaLib:0.3.1")
    // Module - Kether
    installModule("module-kether")
}

taboolib {
    version = taboolibVersion
    // Taboolib 模块
    taboolibModules.forEach { install(it) }
    install("platform-bukkit")
    install("module-nms")
    install("module-nms-util")
    install("expansion-player-fake-op")

    description {
        name(rootName)

        desc("Advanced Minecraft Comprehensive Control")

        contributors {
            name("TheFloodDragon")
        }

        dependencies {
            name("PlaceholderAPI").optional(true)
        }

        links {
            name("homepage").url("https://github.com/$githubRepo")
        }

        @Suppress("UNCHECKED_CAST") val nodes = bukkitNodes as HashMap<String, Any>

        // Taboolib Version
        nodes["taboolib-version"] = taboolibVersion
        // Folia Support
        nodes["folia-supported"] = true
        // Build Info
        nodes["built-date"] = currentISODate
        nodes["built-by"] = systemUserName
        nodes["built-os"] = systemOS
        nodes["built-ip"] = systemIP

    }

    relocate("com.tcoded.folialib.", "$rootGroup.library.folia.folialib_0_3_1.")

    classifier = null
    options("skip-minimize", "keep-kotlin-module", "skip-taboolib-relocate")
}