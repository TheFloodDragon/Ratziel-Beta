plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

dependencies {
    compileTabooLib()
    compileCore(11903)

    //MiniMessage: https://docs.adventure.kyori.net/minimessage/api.html
    adventure()
}

taboolib {
    version = taboolibVersion

    taboolibModules.forEach { install(it) }

    description {
        name(rootName)
        desc("我不知道~")

        contributors {
            name("TheFloodDragon")
        }

        dependencies {
            name("PlaceholderAPI").optional(true)
        }

        bukkitNodes = HashMap<String, Any>().apply { put("api-version", 1.13) }


    }

    classifier = null
    //options("skip-minimize", "keep-kotlin-module", "skip-taboolib-relocate")
}