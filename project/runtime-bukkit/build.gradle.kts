plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

dependencies {
    // 其它
    compileCore(12002)
    compileOnly("net.md-5:bungeecord-chat:1.17")
    compileOnly("me.clip:placeholderapi:2.11.4")
    // Folia
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    taboo("com.tcoded:FoliaLib:0.3.1")
    // Module - Kether
    installModule("module-kether")
}

tasks {
    build { dependsOn(tabooRelocateJar) }
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
            name("homepage").url("https://github.com/TheFloodDragon/Ratziel-Beta/")
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