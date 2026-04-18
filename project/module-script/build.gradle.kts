dependencies {
    // Kotlin Scripting
    compileOnly(kotlin("scripting-common"))
    compileOnly(kotlin("scripting-jvm"))
    compileOnly(kotlin("scripting-jvm-host"))
    compileOnly(kotlin("scripting-compiler-embeddable"))
    compileOnly(kotlin("compiler-embeddable"))
    testImplementation(kotlin("scripting-common"))
    testImplementation(kotlin("scripting-jvm"))
    testImplementation(kotlin("scripting-jvm-host"))
    testImplementation(kotlin("scripting-compiler-embeddable"))
    testImplementation(kotlin("compiler-embeddable"))
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-script-runtime:${rootProject.libs.versions.kotlin.get()}")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:${rootProject.libs.versions.kotlin.get()}")
    // Fluxon
    implementation(libs.fluxon) { isTransitive = false }
    // JavaScript: Nashorn Engine
    compileOnly(libs.nashorn)
    testImplementation(libs.nashorn)
    // JavaScript: GraalJs
    compileOnly(libs.graalvm.polyglot)
    testImplementation(libs.graalvm.polyglot)
    testRuntimeOnly("org.graalvm.truffle:truffle-runtime:${rootProject.libs.versions.graaljs.get()}")
    testRuntimeOnly("org.graalvm.js:js-language:${rootProject.libs.versions.graaljs.get()}")
    // Jexl3: Apache
    compileOnly(libs.jexl)
    testImplementation(libs.jexl)
}

// 资源处理
tasks.processResources {
    filesMatching("**/*.json") {
        expand(
            "kotlinVersion" to rootProject.libs.versions.kotlin.get(),
            "nashornVersion" to rootProject.libs.versions.nashorn.get(),
            "graaljsVersion" to rootProject.libs.versions.graaljs.get(),
            "jexlVersion" to rootProject.libs.versions.jexl.get(),
        )
    }
}
