dependencies {
    testImplementation(kotlin("test-junit5"))
}

tasks.test {
    useJUnitPlatform()
}
