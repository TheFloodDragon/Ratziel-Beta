import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    // Platform - Bukkit
    shadowModule("runtime-bukkit")
    // Extension - Item
    shadowModule("module-item")
}

tasks {
    withType<ShadowJar> {
        archiveFileName.set("$rootName-Bukkit-$rootVersion.jar") //输出名称
    }
}