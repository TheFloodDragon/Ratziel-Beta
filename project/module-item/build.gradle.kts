dependencies {
    implementation(libs.altawk.nbt) { isTransitive = false }
    compileOnly(libs.taboolib.platform.bukkit)
    compileOnly(libs.taboolib.platform.bukkit.impl)
    compileOnly(libs.taboolib.bukkit.nms)
    compileOnly(libs.taboolib.bukkit.nms.stable)
    compileOnly(libs.taboolib.bukkit.util)
    compileOnly(libs.taboolib.bukkit.hook)
    compileOnly(projects.project.runtimeBukkit)
    compileOnly(projects.project.moduleScript)
    compileOnly(projects.project.moduleCompatCore)
    compileOnly("com.google.guava:guava:32.1.2-jre")
    compileNMS()
    compileCore(12005, mapped = true)
    compileCore(12105, mapped = true)
}

tasks.shadowJar {
    from(zipTree(project(":project:module-item:j21").tasks.jar.get().archiveFile))
}