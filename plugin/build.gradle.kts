import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(":project:bukkit"))
}

tasks {
    withType<ShadowJar> {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
        // 删除一些不必要的文件
        exclude("META-INF/maven/**")
        exclude("META-INF/tf/**")
    }
    build {
        dependsOn(shadowJar)
    }
}