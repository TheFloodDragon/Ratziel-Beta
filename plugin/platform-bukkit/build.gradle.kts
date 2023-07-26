import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies { implementation(project(":project:runtime-bukkit")) }

tasks {
    build {
        dependsOn(shadowJar)
    }
    withType<ShadowJar> {
        archiveFileName.set("$rootName-Bukkit-$rootVersion.jar") //输出名称
    }
}