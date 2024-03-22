dependencies {
    // Platform - Bukkit
    shadowModule("runtime-bukkit")
    // Compat
    shadowModule("module-compat-core")
    shadowModule("module-compat-bukkit")
    // Extension - Item
    shadowModule("module-item")
}

tasks.shadowJar {
    archiveFileName.set("$rootName-Bukkit-$rootVersion.jar")
}