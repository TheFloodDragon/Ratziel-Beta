import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    parent!!.subprojects
        .forEach {
            if (name != it.name) implementation(it)
        }
}

tasks {
    withType<ShadowJar> {
        archiveFileName.set("$rootName-$rootVersion.jar")
    }
}