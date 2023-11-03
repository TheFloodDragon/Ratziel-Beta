import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    compileTaboo("platform-bukkit")
    // Folia
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    // FoliaLib
    implementation("com.tcoded:FoliaLib:0.3.1")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    withType<ShadowJar> {
        relocate("com.tcoded.folialib.", "$rootGroup.library.folia.folialib_0_3_1.")
    }
}