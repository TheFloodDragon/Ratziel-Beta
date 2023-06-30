dependencies {
    compileOnly("com.google.guava:guava:31.1-jre")

    rootProject.allprojects.forEach {
        if (it.parent?.name == "project" && !it.name.contains("bungee"))
            implementation(it)
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}