dependencies {
    // Platform - Bukkit
    shadowModule("runtime-bukkit", WithShadow)
    // Extension - Item
    shadowModule("module-item")
}

tasks.shadowJar {
    archiveFileName.set("$rootName-Bukkit-$rootVersion.jar")
}