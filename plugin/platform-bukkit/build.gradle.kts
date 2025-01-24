dependencies {
    // Core / Common
    implementation(projects.project.moduleCore)
    implementation(projects.project.moduleCommon)
    // Platform - Bukkit
    implementation(projects.project.runtimeBukkit)
    // Script
    implementation(projects.project.moduleScript)
    // Compat
    implementation(projects.project.moduleCompatCore)
    taboo(projects.project.moduleCompatInject)
    implementation(projects.project.moduleCompatBukkit)
    // Extension - Item
    implementation(projects.project.moduleItem)
}

taboolib {
    env {
        // Platform - Bukkit
        install("platform-bukkit", "platform-bukkit-impl")
        // Bukkit - Basic
        install("bukkit-hook", "bukkit-util", "bukkit-fake-op", "bukkit-xseries")
        // NMS
        install("bukkit-nms", "bukkit-nms-stable")
        // Script
        install("minecraft-kether")
    }
    description {
        dependencies {
            name("PlaceholderAPI").with("bukkit").optional(true)
        }
    }
}

tasks {
    shadowJar {
        dependencies {
            exclude(project(":project:module-compat-inject"))
        }
        archiveFileName.set("${rootProject.name}-Bukkit-$version.jar")
    }
    jar { dependsOn(project(":project:module-compat-inject").tasks.jar) }
}