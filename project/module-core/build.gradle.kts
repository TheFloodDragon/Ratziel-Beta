plugins {
    kotlin("plugin.serialization") version "1.8.21"
}

dependencies {
    serialization()
    installTaboo("platform-bukkit")
    installTaboo("platform-bungee")
    installTaboo("platform-velocity")
    compileOnly("net.md-5:bungeecord-api:1.20-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
}