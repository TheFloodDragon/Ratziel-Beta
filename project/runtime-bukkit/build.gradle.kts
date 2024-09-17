repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // Core
    compileCore(12005)
    compileTaboo("platform-bukkit")
    compileTaboo("bukkit-nms")
    // Module
    compileModule("module-script")
    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.6")
}