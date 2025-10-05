dependencies {
    compileOnly(libs.altawk.nbt) { isTransitive = false }
    compileOnly(libs.taboolib.platform.bukkit)
    compileOnly(libs.taboolib.platform.bukkit.impl)
    compileOnly(libs.taboolib.bukkit.nms)
    compileOnly(libs.taboolib.bukkit.nms.stable)
    compileOnly(libs.taboolib.bukkit.util)
    compileOnly(libs.taboolib.bukkit.hook)
    compileOnly(projects.project.runtimeBukkit)
    compileOnly(projects.project.moduleScript)
    compileOnly(projects.project.moduleItem)
    compileNMS()
    compileCore(12005, mapped = true)
}

// TODO Only for test
tasks {
    // ShadowJar 基础配置
    shadowJar {
        // Options
        archiveAppendix.set("")
        archiveClassifier.set("")
        archiveVersion.set(version.toString())
        destinationDirectory.set(file("$rootDir/outs"))
        // Taboolib
        relocate("taboolib", "${rootProject.group}.taboolib")
        // NBT
        relocate("cn.altawk.nbt.", "${rootProject.group}.module.nbt.")
        // 删除模块元数据
        exclude("META-INF/*.kotlin_module")
    }
    build { dependsOn(shadowJar) }
}
