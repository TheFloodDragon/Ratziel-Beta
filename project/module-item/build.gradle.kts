dependencies {
    compileNMS()
//    compileCore(12005, mapped = true)
//    compileCore(12005)
    compileOnly("com.google.guava:guava:32.1.2-jre")
    compileTaboo("platform-bukkit")
    compileTaboo("module-nms")
    compileTaboo("module-nms-util-stable")
    compileTaboo("module-bukkit-xseries")
    compileTaboo("module-bukkit-util")
    compileTaboo("module-bukkit-hook")
    compileModule("runtime-bukkit")
    compileOnly(fileTree("libs"))
}