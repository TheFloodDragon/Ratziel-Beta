dependencies {
    implementation("cn.altawk.nbt:tag:0.0.0") {
        isTransitive = false
    }
    compileTaboo("platform-bukkit")
    compileTaboo("platform-bukkit-impl")
    compileTaboo("bukkit-nms")
    compileTaboo("bukkit-nms-stable")
    compileTaboo("bukkit-xseries")
    compileTaboo("bukkit-util")
    compileTaboo("bukkit-hook")
    compileModule("runtime-bukkit")
    compileModule("module-script")
    compileModule("module-compat-core")
    compileOnly("com.google.guava:guava:32.1.2-jre")
    compileNMS()
    compileCore(12005, mapped = true)
    compileCore(12101, mapped = true)
    compileOnly(fileTree("libs"))
}