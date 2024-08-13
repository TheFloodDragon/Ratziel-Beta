dependencies {
    // Kotlin Script
    compileOnly(kotlin("scripting-common"))
    compileOnly(kotlin("scripting-jvm"))
    compileOnly(kotlin("scripting-jvm-host"))
    compileOnly(kotlin("scripting-compiler-embeddable"))
    compileOnly(kotlin("compiler-embeddable"))
    // Kether: Taboolib
//    compileTaboo("minecraft-kether") TODO 6.2
    // JavaScript: Nashorn Engine
    compileOnly("org.openjdk.nashorn:nashorn-core:15.4")
    // Jexl3: Apache
    compileOnly("org.apache.commons:commons-jexl3:3.4.0")
}