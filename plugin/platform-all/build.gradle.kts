dependencies {
    parent!!.subprojects
        .forEach {
            if (name != it.name) implementation(it)
        }
}

tasks.shadowJar {
    archiveFileName.set("$rootName-$rootVersion.jar")
}