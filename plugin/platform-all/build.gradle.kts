import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    parent!!.subprojects
        .forEach {
            if (name != it.name) implementation(it)
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