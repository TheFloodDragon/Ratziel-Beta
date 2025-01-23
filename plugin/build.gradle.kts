plugins {
    alias(libs.plugins.taboolib)
}

subprojects {

    // Apply Taboolib Plugin
    apply(plugin = "io.izzel.taboolib")

    // Taboolib Settings
    taboolib {

        // 版本参数设置
        version {
            taboolib = rootProject.libs.versions.taboolib.get()
            coroutines = rootProject.libs.versions.coroutines.get()
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
            rootProject.libs.bundles.taboolib.get().forEach { install(it.name) }
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
        shadowJar {
            dependsOn(taboolibMainTask)
            from(taboolibMainTask.get().inJar)
            dependencies {
                exclude {
                    it.moduleGroup == "io.izzel.taboolib"
                }
            }
            combineFiles.forEach { append(it) }
        }
        build { dependsOn(shadowJar) }
    }

}

buildDirClean()