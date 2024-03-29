val platforms = parent!!.subprojects.filter { name != it.name }

dependencies {
    platforms.forEach { taboo(project(":plugin:${it.name}", "shadow")) }
}

taboolib {
    env {
        platforms.flatMap { it.taboolib.env.modules }.forEach { install(it) }
    }
}

tasks.shadowJar {
    archiveFileName.set("$rootName-$rootVersion.jar")
}