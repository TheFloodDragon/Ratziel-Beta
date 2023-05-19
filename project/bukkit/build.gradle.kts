plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

dependencies {
    compileCore(11903)
    adventure()

    implementation(project(":project:core"))

    compileOnly("com.google.guava:guava:31.1-jre")
}

taboolib {
    version = taboolibVersion

    taboolibModules.forEach { install(it) }
    install("platform-bukkit")

    description {
        name(rootName)
        desc("我不知道~")

        contributors {
            name("TheFloodDragon")
        }

        dependencies {
            name("PlaceholderAPI").optional(true)
        }

    }

    classifier = null
    options("skip-minimize", "keep-kotlin-module", "skip-taboolib-relocate")
}