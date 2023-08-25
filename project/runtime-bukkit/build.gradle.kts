plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

dependencies {
    compileOnly("com.google.guava:guava:31.1-jre")
    // 模块依赖
    installModule("module-kether")
}

taboolib {
    version = taboolibVersion
    taboolibModules.forEach { install(it) }
    install("platform-bukkit")
    install("module-kether")

    description {
        name = rootName

        contributors {
            name("TheFloodDragon")
        }

        dependencies {
//            name("PlaceholderAPI").optional(true)
//            name("NeigeItems").optional(true)
//            name("Zaphkiel").optional(true)
//            name("HeadDatabase").optional(true)
//            name("Oraxen").optional(true)
//            name("ItemsAdder").optional(true)
        }

    }

    classifier = null
    options("skip-minimize", "keep-kotlin-module", "skip-taboolib-relocate")
}