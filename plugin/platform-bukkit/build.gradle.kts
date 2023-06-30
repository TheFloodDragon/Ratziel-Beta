import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(":project:runtime-bukkit"))
}

tasks {
    withType<ShadowJar> {
        // Options
        archiveAppendix.set("")
        archiveClassifier.set("")
        archiveVersion.set(rootVersion)
        //archiveBaseName.set("$rootName-Bukkit")
        // Exclude
        exclude("META-INF/**")
        exclude("com/**", "org/**")
        // Adventure (不需要,因为是动态加载)
        //relocate("net.kyori", "$rootGroup.common.adventure")
        // Taboolib
        relocate("taboolib", "$rootGroup.taboolib")
        relocate("tb", "$rootGroup.taboolib")
        relocate("org.tabooproject", "$rootGroup.taboolib.library")
        // Kotlin
        relocate("kotlin.", "kotlin1820.") { exclude("kotlin.Metadata") }
        relocate("kotlinx.serialization", "kotlinx150.serialization")
    }
    build {
        dependsOn(shadowJar)
    }
}