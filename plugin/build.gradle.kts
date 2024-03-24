subprojects {

    dependencies {
        // 通用模块
        shadowModule("module-core")
        shadowModule("module-common")
    }

    tasks {
        build { dependsOn(shadowJar) }
        shadowJar {
            destinationDirectory.set(file("$rootDir/outs"))
            combineFiles.forEach { append(it) }
        }
    }

}

buildDirClean()