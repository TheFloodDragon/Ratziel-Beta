import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

extra["runtimes"] =
    rootProject.allprojects
        .filter { it.name.contains("runtime") }
        .map { it.name }

dependencies {
    parent!!.subprojects.forEach {
        if (it != this.project()) //排除自己
            implementation(it)
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    withType<ShadowJar> {
        archiveFileName.set("$rootName-$rootVersion.jar") //输出名称
    }
}