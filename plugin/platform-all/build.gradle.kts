// All Platforms
val platforms = arrayOf(
    projects.plugin.platformBukkit
).map { project(it.path) }

dependencies {
    platforms.forEach { implementation(it) }
}

tasks.shadowJar {
    dependsOn(*platforms.map { it.tasks.shadowJar }.toTypedArray())
    archiveFileName.set("${rootProject.name}-$version.jar")
}