dependencies {
    compileCore(12004)
    compileOnly(projects.project.moduleCompatCore)
    // Local libraries
    compileOnly(fileTree("libs"))
}