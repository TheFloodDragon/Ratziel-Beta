plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

dependencies {

    compileOnly("com.google.guava:guava:31.1-jre")

//    rootProject.allprojects.forEach {
//        if (it.parent?.name == "project" && !it.name.contains("bungee"))
//            implementation(it)
//    }
    parent!!.childProjects.values.filter {
        it.name.contains("module")
    }.forEach { implementation(it) }
}

taboolib {
    version = taboolibVersion
    taboolibModules.forEach { install(it) }
    install("platform-bukkit") //Bukkit

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