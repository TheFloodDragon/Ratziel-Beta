// All Platforms
val platforms = arrayOf("platform-bukkit").map { project(":plugin:$it") }

dependencies {
    platforms.forEach { implementation(it) }
}

tasks.shadowJar {
    dependsOn(platforms[0].tasks.shadowJar)
    archiveFileName.set("${rootProject.name}-$version.jar")
}