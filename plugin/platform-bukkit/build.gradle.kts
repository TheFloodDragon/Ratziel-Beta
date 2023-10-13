import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    // Extension - Item
}

tasks {
    withType<ShadowJar> {
        archiveFileName.set("$rootName-Bukkit-$rootVersion.jar") //输出名称
    }
}