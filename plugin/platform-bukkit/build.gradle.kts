import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val runtime: String by extra { "runtime-bukkit" }

dependencies {
    compileOnly("org.yaml:snakeyaml:2.1")
    installModule(runtime)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    withType<ShadowJar> {
        archiveFileName.set("$rootName-Bukkit-$rootVersion.jar") //输出名称
    }
}