repositories {
    maven("https://repo-momi.gtemc.cn/releases/")
}

dependencies {
    compileOnly(projects.project.moduleCompatCore)
    compileOnly(projects.project.runtimeBukkit)
    compileOnly(projects.project.moduleItem)
    compileCore(12104)
    implementation("cn.gtemc:itembridge:1.0.18")
    compileOnly(fileTree("libs"))
}

tasks.jar {
    from(zipTree(project(":project:module-compat-bukkit:j21").tasks.shadowJar.get().archiveFile))
}