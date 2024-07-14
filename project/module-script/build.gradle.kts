dependencies {
    // Kotlin Script
    compileOnly(kotlin("scripting-common"))
    compileOnly(kotlin("scripting-jvm"))
    compileOnly(kotlin("scripting-jvm-host"))
    compileOnly(kotlin("scripting-compiler"))
    compileOnly(kotlin("scripting-jsr223"))
    compileOnly("org.jetbrains.kotlin:kotlin-main-kts:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:$kotlinVersion")
    // Kether: Taboolib
    compileTaboo("module-kether")
    // JavaScript: Nashorn Engine
    compileOnly("org.openjdk.nashorn:nashorn-core:15.4")
    // Jexl3: Apache
    compileOnly("org.apache.commons:commons-jexl3:3.4.0")
}