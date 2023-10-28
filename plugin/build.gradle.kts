import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

subprojects {

    dependencies {
        // 通用模块
        shadowModule("module-core")
        shadowModule("module-common")
    }

    tasks {
        build {
            dependsOn(shadowJar)
        }
        withType<ShadowJar> {
            combineFiles.forEach {
                append(it)
            }
        }
    }

}

buildDirClean()