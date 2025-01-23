rootProject.name = "Ratziel"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Project
applyAll("project")
// Plugin
include("plugin:platform-all")
include("plugin:platform-bukkit")

fun applyAll(name: String) {
    File(rootDir, name).listFiles()?.filter { it.isDirectory && it.name != "build" }?.forEach {
        include("$name:${it.name}")
    }
}