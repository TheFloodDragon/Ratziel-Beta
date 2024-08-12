import io.izzel.taboolib.gradle.UNIVERSAL

plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

subprojects {

    // Apply Taboolib Plugin
    apply(plugin = "io.izzel.taboolib")

    // Taboolib Settings
    taboolib {

        // 版本参数设置
        version {
            taboolib = taboolibVersion
            coroutines = coroutineVersion
            skipKotlinRelocate = true
        }

        // 模块环境设置
        env {
            // 镜像中央仓库
            repoCentral = "https://repo.huaweicloud.com/repository/maven/"
            // Debug Mode
            debug = true
            // Isolated Mode
            enableIsolatedClassloader = true
            // Common Modules
            install(taboolibModules)
        }

        description {
            name(rootName)

            desc("Advanced Minecraft Comprehensive Control")

            contributors {
                name("TheFloodDragon")
            }

            links {
                name("homepage").url("https://github.com/TheFloodDragon/Ratziel-Beta")
            }

            // Nodes
            @Suppress("UNCHECKED_CAST")
            arrayOf(
                bukkitNodes as HashMap<String, Any>,
                bungeeNodes as HashMap<String, Any>
            ).forEach {
                // Build Info
                it["built-date"] = currentISODate
                it["built-by"] = systemUserName
                it["built-os"] = systemOS
                it["built-ip"] = systemIP
            }

        }

    }

    tasks {
        jar { allModules.forEach { dependsOn(it.tasks.jar) } }
        shadowJar {
            dependsOn(taboolibMainTask)
            from(taboolibMainTask.get().inJar)
            combineFiles.forEach { append(it) }
        }
        build { dependsOn(shadowJar) }
    }

}

buildDirClean()