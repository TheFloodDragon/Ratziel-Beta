dependencies {
    // Platform - Bukkit
    shadowModule("runtime-bukkit", ShadowConfig)
    // Extension - Item
    shadowModule("module-item")
}

tasks.shadowJar {
    archiveFileName.set("$rootName-Bukkit-$rootVersion.jar")
}