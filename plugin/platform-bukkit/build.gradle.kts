dependencies { implementation(project(":project:runtime-bukkit")) }

tasks {
    build {
        dependsOn(shadowJar)
    }
}