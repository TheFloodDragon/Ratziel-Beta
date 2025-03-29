dependencies {
    implementation("com.github.TheFloodDragon.nbt:nbt-jvm:0.1.0") { isTransitive = false }
    compileOnly(libs.taboolib.platform.bukkit)
    compileOnly(libs.taboolib.platform.bukkit.impl)
    compileOnly(libs.taboolib.bukkit.nms)
    compileOnly(libs.taboolib.bukkit.nms.stable)
    compileOnly(libs.taboolib.bukkit.xseries)
    compileOnly(libs.taboolib.bukkit.util)
    compileOnly(libs.taboolib.bukkit.hook)
    compileOnly(projects.project.runtimeBukkit)
    compileOnly(projects.project.moduleScript)
    compileOnly(projects.project.moduleCompatCore)
    compileOnly("com.google.guava:guava:32.1.2-jre")
    compileNMS()
    compileCore(12005, mapped = true)
    compileCore(12101, mapped = true)
    compileOnly(fileTree("libs"))
}