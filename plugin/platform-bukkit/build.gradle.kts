import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

extra["runtimes"] = listOf("runtime-bukkit")

tasks {
    build {
        dependsOn(shadowJar)
    }
    withType<ShadowJar> {
        archiveFileName.set("$rootName-Bukkit-$rootVersion.jar") //输出名称
    }
}