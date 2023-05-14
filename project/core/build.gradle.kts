plugins {
    id("io.izzel.taboolib") version taboolibPluginVersion
}

dependencies {
    compileTabooLib()
    compileCore(11903)

    //MiniMessage: https://docs.adventure.kyori.net/minimessage/api.html
    adventure()
}