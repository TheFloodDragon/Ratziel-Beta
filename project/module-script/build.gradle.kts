dependencies {
    // Kether: Taboolib
    compileTaboo("module-kether")
    // JavaScript: Nashorn Engine
    compileOnly("org.openjdk.nashorn:nashorn-core:15.4")
    // Jexl3: Apache
    compileOnly("org.apache.commons:commons-jexl3:3.4.0")
}