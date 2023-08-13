plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies { compileOnly("org.yaml:snakeyaml:2.1") }

@Suppress("DEPRECATION") gradle.buildFinished { buildDir.deleteRecursively() }