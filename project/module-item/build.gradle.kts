dependencies {
    compileNMS()
    compileCore(12002)
    // ASM
    compileOnly("org.ow2.asm:asm:9.4")
    compileOnly("org.ow2.asm:asm-util:9.4")
    compileOnly("org.ow2.asm:asm-commons:9.4")
    compileOnly("com.google.guava:guava:32.1.2-jre")
    compileTaboo("platform-bukkit")
    compileTaboo("module-nms")
    compileTaboo("module-nms-util")
    compileModule("runtime-bukkit")
}