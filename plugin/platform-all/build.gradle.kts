dependencies {
    parent!!.subprojects
        .forEach {
            if (name != it.name) implementation(project(it.path, ByShadow))
        }
}

tasks.shadowJar {
    archiveFileName.set("$rootName-$rootVersion.jar")
}