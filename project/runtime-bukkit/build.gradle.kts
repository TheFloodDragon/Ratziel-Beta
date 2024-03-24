import io.izzel.taboolib.gradle.*

plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

dependencies {
    // Compat
    tabooModule("module-compat-core")
    tabooModule("module-compat-bukkit")
    // Core
    compileCore(12004)
    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.4")
}

taboolib {

    // 版本参数设置
    version {
        taboolib = taboolibVersion
        coroutines = coroutineVersion
        skipKotlinRelocate = true
    }

    // 模块环境设置
    env {
        // Debug Mode
        debug = true
        // Module Dependencies
        install(UNIVERSAL, BUKKIT_ALL, KETHER, NMS_UTIL, EXPANSION_PLAYER_FAKE_OP)
        enableIsolatedClassloader = true
    }

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

        @Suppress("UNCHECKED_CAST")
        val nodes = bukkitNodes as HashMap<String, Any>

        // Build Info
        nodes["built-date"] = currentISODate
        nodes["built-by"] = systemUserName
        nodes["built-os"] = systemOS
        nodes["built-ip"] = systemIP

    }

}