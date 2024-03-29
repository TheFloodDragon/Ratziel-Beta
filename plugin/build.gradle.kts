subprojects {

    dependencies {
        // 通用模块
        shadowModule("module-core")
        shadowModule("module-common")
    }

    tasks {
        build { dependsOn(shadowJar) }
        shadowJar { combineFiles.forEach { append(it) } }
    }

}

buildDirClean()