import io.izzel.taboolib.gradle.BUKKIT_ALL
import io.izzel.taboolib.gradle.EXPANSION_PLAYER_FAKE_OP
import io.izzel.taboolib.gradle.NMS_UTIL
import io.izzel.taboolib.gradle.UNIVERSAL

plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

dependencies {
    compileCore(12002)
    compileOnly("com.google.code.gson:gson:2.8.9")
    compileOnly("net.md-5:bungeecord-chat:1.17")
    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.4")
    // Module - Kether
    installModule("module-kether")
    compileOnly("io.github.altawk.asl:script-kether:$taboolibVersion")
}

taboolib {

    // 版本参数设置
    version {
        taboolib = taboolibVersion
        coroutines = coroutineVersion
        // Skip then entrust to ShadowJar
        skipTabooLibRelocate = true
        skipKotlinRelocate = true
    }

    // 模块环境设置
    env {
        // Debug Mode
        debug = true
        // Module Dependencies
        install(UNIVERSAL, BUKKIT_ALL, NMS_UTIL, EXPANSION_PLAYER_FAKE_OP)
        // Incomplete Isolated
        enableIsolatedClassloader = false
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

        @Suppress("UNCHECKED_CAST") val nodes = bukkitNodes as HashMap<String, Any>

        // Taboolib Version
        nodes["taboolib-version"] = taboolibVersion
        // Build Info
        nodes["built-date"] = currentISODate
        nodes["built-by"] = systemUserName
        nodes["built-os"] = systemOS
        nodes["built-ip"] = systemIP

    }

}