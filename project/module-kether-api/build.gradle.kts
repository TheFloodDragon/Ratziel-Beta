import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    shadowTaboo("module-kether")
    shadowTaboo("expansion-javascript")
}

tasks {
    build{
        dependsOn(shadowJar)
    }
    withType<ShadowJar> {
        archiveVersion.set(rootVersion)
        destinationDirectory.set(file("$rootDir/outs")) //输出路径
        /**
         * 删除关于Bukkit的内容
         */
        exclude("taboolib/module/kether/action/game/**")
    }
}