import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.configurationcache.extensions.capitalized

subprojects {
    tasks {
        build {
            dependsOn(shadowJar)
        }
        withType<ShadowJar> {
            archiveFileName.set("Script-${project.name.capitalized()}-${project.version}.jar") //输出名称
        }
    }

}

buildDirClean()