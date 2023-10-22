dependencies {
    compileNMS()
    compileCore(12002)
    compileOnly("com.google.guava:guava:32.1.2-jre")
    compileTaboo("platform-bukkit")
    compileTaboo("module-nms")
    compileTaboo("module-nms-util")
    compileModule("runtime-bukkit")
    compileOnly("org.bukkit:craftbukkit:1.12.2-R0.1-SNAPSHOT")
}