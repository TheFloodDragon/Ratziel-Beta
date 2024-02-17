dependencies {
    compileCore(12004)
    compileModule("module-compat-core")
    // Local libraries
    compileOnly(fileTree("libs"))
}