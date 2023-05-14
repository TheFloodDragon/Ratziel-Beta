dependencies {
    compileTabooLib()
    compileCore(11903)
    adventure()

    compileOnly(project(":project:core"))

    compileOnly("com.google.guava:guava:31.1-jre")
}