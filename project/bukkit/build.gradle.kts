dependencies {
    compileTabooLib()
    compileCore(11903)

    compileOnly(project(":project:core"))

//    //MiniMessage: https://docs.adventure.kyori.net/minimessage/api.html
//    compileOnly("net.kyori:adventure-api:4.12.0")
//    compileOnly("net.kyori:adventure-platform-bukkit:4.2.0")
//    compileOnly("net.kyori:adventure-text-minimessage:4.12.0")
//
//    compileOnly("com.google.guava:guava:31.1-jre")
}