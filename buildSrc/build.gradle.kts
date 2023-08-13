plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies { implementation("org.yaml:snakeyaml:2.1") }

@Suppress("DEPRECATION") gradle.buildFinished { buildDir.deleteRecursively() }