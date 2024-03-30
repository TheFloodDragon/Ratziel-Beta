import io.izzel.taboolib.gradle.UNIVERSAL

plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

subprojects {

    // Apply Taboolib Plugin
    apply(plugin = "io.izzel.taboolib")

    // Common Dependencies
    dependencies {
        // 通用模块
        shadowModule("module-core")
        shadowModule("module-common")
    }

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
            // Debug Mode
            debug = true
            // Isolated Mode
            enableIsolatedClassloader = true
            // Common Modules
            install(UNIVERSAL)
        }

        description {
            name(rootName)

            desc("Advanced Minecraft Comprehensive Control")

            contributors {
                name("TheFloodDragon")
            }

            links {
                name("homepage").url("https://github.com/$githubRepo")
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
        jar { allModules.onEach { dependsOn(it.tasks.jar) } }
        shadowJar {
            dependsOn(taboolibMainTask)
            from(taboolibMainTask.get().inJar)
            combineFiles.onEach { append(it) }
        }
        build { dependsOn(shadowJar) }
    }

}

buildDirClean()