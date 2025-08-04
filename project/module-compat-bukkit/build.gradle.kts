dependencies {
    compileOnly(projects.project.moduleCompatCore)
    compileOnly(projects.project.runtimeBukkit)
    compileOnly(projects.project.moduleItem)
    compileCore(12104)
    compileOnly(fileTree("libs"))
}

tasks.jar {
    from(zipTree(project(":project:module-compat-bukkit:j21").tasks.jar.get().archiveFile))
}