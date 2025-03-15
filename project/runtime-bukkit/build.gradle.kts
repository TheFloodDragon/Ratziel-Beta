dependencies {
    // Core
    compileCore(12005)
    compileOnly(libs.taboolib.platform.bukkit)
    compileOnly(libs.taboolib.platform.bukkit.impl)
    compileOnly(libs.taboolib.bukkit.nms)
    // Module
    compileOnly(projects.project.moduleScript)
    // PlaceholderAPI
    compileOnly("public:PlaceholderAPI:2.10.9")
}