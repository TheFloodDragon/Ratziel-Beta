dependencies {
    parent!!.childProjects.values.filter {
        it.name.contains("platform")
    }.forEach { implementation(it) }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}