plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradle.buildFinished { buildDir.deleteRecursively() }