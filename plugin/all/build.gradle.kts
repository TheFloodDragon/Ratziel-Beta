import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    parent!!.childProjects.values.filter {
        it.name.contains("platform")
    }.forEach { implementation(it) }
}

tasks {
    withType<ShadowJar> {
        archiveVersion.set(rootVersion)
    }
    build {
        dependsOn(shadowJar)
    }
}