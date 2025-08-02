dependencies {
    compileOnly(projects.project.moduleCompatCore)
    compileOnly(projects.project.runtimeBukkit)
    compileOnly(projects.project.moduleItem)
    compileCore(12104)
    compileOnly(fileTree("libs"))
}