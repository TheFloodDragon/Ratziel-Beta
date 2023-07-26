import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

subprojects {
    tasks {
        build {
            dependsOn(shadowJar)
        }
        withType<ShadowJar> {
            archiveFileName.set("Script-${project.name.uppercase()}-${project.version}.jar") //输出名称
        }
    }

}

buildDirClean()