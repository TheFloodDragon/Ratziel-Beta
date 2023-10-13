import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    rootProject.allprojects
//        .forEach { if (it.name.contains("platform")) shadow(it) }
}

tasks {
    withType<ShadowJar> {
        archiveFileName.set("$rootName-$rootVersion.jar") //输出名称
    }
}