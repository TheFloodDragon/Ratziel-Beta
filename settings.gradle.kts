rootProject.name = "Ratziel"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

applyAll("project")
applyAll("plugin")
applyAll("script")

fun applyAll(name: String) {
    File(rootDir, name).listFiles()?.filter { it.isDirectory }?.forEach {
        include("$name:${it.name}")
    }
}