plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

dependencies {
    compileTabooLib()
    compileCore(11903)

    //MiniMessage: https://docs.adventure.kyori.net/minimessage/api.html
    compileOnly("net.kyori:adventure-api:4.12.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.2.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.12.0")

    compileOnly("com.google.guava:guava:31.1-jre")
}

taboolib {
    version = taboolibVersion

    taboolibModules.forEach { install(it) }

    description {
        name = rootName

        contributors {
            name("TheFloodDragon")
        }

        dependencies {
            name("PlaceholderAPI").optional(true)
        }

        bukkitNodes = HashMap<String, Any>().apply { put("api-version", 1.13) }


    }

    classifier = null
    options("skip-minimize", "keep-kotlin-module", "skip-taboolib-relocate")
}
