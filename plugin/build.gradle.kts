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
            debug = false
            // Isolated Mode
            enableIsolatedClassloader = true
            // Common Modules
            rootProject.libs.bundles.taboolib.get().forEach { install(it.name) }
        }

        description {
            name(rootProject.name)

            desc("Advanced Minecraft Comprehensive Control")

            contributors {
                name("TheFloodDragon")
            }

            links {
                name("homepage").url("https://github.com/TheFloodDragon/Ratziel-Beta")
            }

            // Nodes
            @Suppress("UNCHECKED_CAST")
            arrayOf(bukkitNodes, bungeeNodes).map { it as MutableMap<Any, Any> }.forEach {
                // Build Info
                it["built-date"] = currentISODate
                it["built-by"] = systemUserName
                it["built-os"] = systemOS
                it["built-ip"] = systemIP
            }

        }

    }

    tasks {
        // ShadowJar 基础配置
        shadowJar {
            dependsOn(taboolibMainTask)
            dependencies {
                exclude {
                    it.moduleGroup == "io.izzel.taboolib"
                }
            }

            // 合并配置文件
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            mergeServiceFiles()
            setOf(
                "settings.yml",
                "lang/zh_CN.yml",
                "lang/en_US.yml",
            ).forEach { append(it) }

            // Options
            archiveAppendix.set("")
            archiveClassifier.set("")
            archiveVersion.set(version.toString())
            destinationDirectory.set(file("$rootDir/outs"))
            // Taboolib
            relocate("taboolib", "${rootProject.group}.taboolib")
            // NBT
            relocate("cn.altawk.nbt.", "${rootProject.group}.module.nbt.")
            // ItemBridge: https://github.com/jhqwqmc/ItemBridge
            relocate("cn.gtemc.itembridge.", "${rootProject.group}.libraries.itembridge.")
            // Fluxon
            relocate("org.tabooproject.fluxon.", "${rootProject.group}.libraries.fluxon.")
            // 删除模块元数据
            exclude("META-INF/*.kotlin_module")
        }
        build { dependsOn(shadowJar) }
    }

}

buildDirClean()