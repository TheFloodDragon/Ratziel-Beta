import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(":project:bukkit"))
}

tasks {
    withType<ShadowJar> {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
        // 删除一些不必要的文件
        exclude("META-INF/**")
        exclude("com/**", "org/**")
        // adventure
        relocate("net.kyori", "$rootGroup.common.adventure")
        // taboolib
        relocate("taboolib", "$rootGroup.taboolib")
        relocate("tb", "$rootGroup.taboolib")
        relocate("org.tabooproject", "$rootGroup.taboolib.library")
        // kotlin
        relocate("kotlin.", "kotlin1810.") { exclude("kotlin.Metadata") }
        relocate("kotlinx.serialization", "kotlinx150.serialization")
    }
    build {
        dependsOn(shadowJar)
    }
}