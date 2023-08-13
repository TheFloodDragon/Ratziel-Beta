plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies { compileOnly("org.yaml:snakeyaml:2.0") }

@Suppress("DEPRECATION") gradle.buildFinished { buildDir.deleteRecursively() }