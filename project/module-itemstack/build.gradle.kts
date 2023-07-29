plugins {
    kotlin("plugin.serialization") version "1.8.21"
}

dependencies {
    serialization()
    compileModule("module-core")
    compileModule("module-common")
    installTaboo("platform-bukkit")
}