repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // Core
    compileCore(12005)
    compileOnly(libs.taboolib.platform.bukkit)
    compileOnly(libs.taboolib.platform.bukkit.impl)
    compileOnly(libs.taboolib.bukkit.nms)
    // Module
    compileOnly(projects.project.moduleScript)
    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.6")
}