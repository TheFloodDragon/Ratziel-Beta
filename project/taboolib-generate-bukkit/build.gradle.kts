plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

taboolib {
    version = taboolibVersion
    taboolibModules.forEach { install(it) }
    install("platform-bukkit")

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