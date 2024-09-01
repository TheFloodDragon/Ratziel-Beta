dependencies {
    compileNMS()
//    compileCore(12005, mapped = true)
//    compileCore(12005)
    compileOnly("com.google.guava:guava:32.1.2-jre")
    compileTaboo("platform-bukkit")
    compileTaboo("platform-bukkit-impl")
    compileTaboo("nms")
//    compileTaboo("nms-util-stable") TODO 6.2
    compileTaboo("bukkit-xseries")
    compileTaboo("bukkit-xseries-item")
    compileTaboo("bukkit-util")
    compileTaboo("bukkit-hook")
    compileModule("runtime-bukkit")
    compileModule("module-script")
    compileModule("module-compat-core")
    compileOnly(fileTree("libs"))
}