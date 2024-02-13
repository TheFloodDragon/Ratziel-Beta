dependencies {
    // Platform - Bukkit
    shadowModule("runtime-bukkit")
    // Compat
    shadowModule("module-compat-core")
    // Extension - Item
    shadowModule("module-item")
}

tasks.shadowJar {
    archiveFileName.set("$rootName-Bukkit-$rootVersion.jar")
}