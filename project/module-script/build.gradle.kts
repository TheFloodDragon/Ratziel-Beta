dependencies {
    // Kotlin Script
    compileOnly(kotlin("scripting-common"))
    compileOnly(kotlin("scripting-jvm"))
    compileOnly(kotlin("scripting-jvm-host"))
    compileOnly(kotlin("scripting-compiler-embeddable"))
    compileOnly(kotlin("compiler-embeddable"))
    // Kether: Taboolib
    compileOnly(libs.taboolib.minecraft.kether)
    // JavaScript: Nashorn Engine
    compileOnly(libs.nashorn)
    // Jexl3: Apache
    compileOnly(libs.jexl)
}

// 资源处理
tasks.processResources {
    filesMatching("**/*.json") {
        expand(
            "kotlinVersion" to rootProject.libs.versions.kotlin.get(),
            "nashornVersion" to rootProject.libs.versions.nashorn.get(),
            "jexlVersion" to rootProject.libs.versions.jexl.get(),
        )
    }
}