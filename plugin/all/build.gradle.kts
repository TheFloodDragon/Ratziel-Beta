import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    parent!!.childProjects.values.filter {
        it.name.contains("platform")
    }.forEach { implementation(it) }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    withType<ShadowJar> {
        archiveFileName.set("$rootName-$rootVersion.jar") //输出名称
    }
}