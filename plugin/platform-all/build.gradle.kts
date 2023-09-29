import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

extra["runtimes"] =
    rootProject.allprojects
        .filter { it.name.contains("runtime") }
        .map { it.name }

tasks {
    build {
        dependsOn(shadowJar)
    }
    withType<ShadowJar> {
        archiveFileName.set("$rootName-$rootVersion.jar") //输出名称
    }
}