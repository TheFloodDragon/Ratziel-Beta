import java.text.SimpleDateFormat

const val rootName = "Ratziel"
const val rootGroup = "cn.fd.ratziel"
const val rootVersion = "A.0.0.8.1"

const val kotlinVersion = "1.9.10"
const val serializationVersion = "1.6.0"
const val coroutineVersion = "1.7.3"
const val shadowJarVersion = "8.1.1"
const val taboolibPluginVersion = "1.56"

val taboolibVersion = getLatestRelease(
    "TabooLib", "taboolib",
    fallback = "6.0.12-26" // 如果获取不到最新版本就改这个
).also { println("Using taboolib-version = $it") }

val isoInstantFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

val taboolibModules = setOf(
    "common",
    "common-5",
    "module-configuration",
    "module-lang",
    "module-chat",
    "expansion-command-helper"
)

val adventureModules = setOf(
    "net.kyori:adventure-api:4.14.0",
//    "net.kyori:adventure-platform-api:4.3.0",
    "net.kyori:adventure-platform-bukkit:4.3.0",
//    "net.kyori:adventure-platform-facet:4.3.0",
    "net.kyori:adventure-text-minimessage:4.14.0",
//    "net.kyori:adventure-text-serializer-gson:4.14.0",
//    "net.kyori:adventure-text-serializer-bungeecord:4.3.0",
//    "net.kyori:adventure-text-serializer-legacy:4.14.0"
)