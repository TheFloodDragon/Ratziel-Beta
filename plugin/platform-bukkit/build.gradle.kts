// Runtime - Bukkit
val runtime = project(":project:runtime-bukkit")

dependencies {
    // Platform - Bukkit
    implementation(runtime)
    // Extension - Item
    shadowModule("module-item")
}

runtime.tasks.jar {
    dependsOn(allModules.filter { it.name != runtime.name }.map { it.tasks.jar.name })
}

tasks.shadowJar {
    archiveFileName.set("$rootName-Bukkit-$rootVersion.jar")
}