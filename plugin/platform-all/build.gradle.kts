import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    parent!!.childProjects.values.filter {
        it != project //排除自身
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