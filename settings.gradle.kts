rootProject.name = "Ratziel"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Project
applyAll("project")
// Plugin
include("plugin:platform-all")
include("plugin:platform-bukkit")

fun applyAll(name: String) {
    File(rootDir, name).walk().filter { f ->
        f.isDirectory && f.listFiles().any { it.nameWithoutExtension.endsWith("gradle") }
    }.forEach {
        include(it.relativeTo(rootDir).path.replace('\\', ':'))
    }
}