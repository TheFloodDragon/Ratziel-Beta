import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.configurationcache.extensions.capitalized

subprojects {

    dependencies {
        compileModule("module-script")
    }

    tasks {
        build {
            dependsOn(shadowJar)
        }
        shadowJar {
            archiveFileName.set("Script-${project.name.capitalized()}-${project.version}.jar") //输出名称
        }
    }

}