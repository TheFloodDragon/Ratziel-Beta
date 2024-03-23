subprojects {

    tasks {
        build { dependsOn(shadowJar) }
        shadowJar { combineFiles.forEach { append(it) } }
    }

}

buildDirClean()