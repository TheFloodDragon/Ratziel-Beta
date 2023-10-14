rootProject.name = "Ratziel"

apply("project")
apply("plugin")
apply("script")

fun apply(name: String) {
    File(rootDir, name).listFiles()?.filter { it.isDirectory }?.forEach {
        include("$name:${it.name}")
    }
}