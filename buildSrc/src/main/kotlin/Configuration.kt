const val rootName = "Ratziel"
const val rootGroup = "cn.fd.ratziel"
const val rootVersion = "A.0.2.1.6"

const val kotlinVersion = "1.9.21"
const val serializationVersion = "1.6.2"
const val coroutineVersion = "1.7.3"
const val shadowJarVersion = "8.1.1"
const val taboolibPluginVersion = "1.56"

val taboolibVersion = getLatestRelease("TabooLib", "taboolib").also { println("Using taboolib-version = $it") }

val isoInstantFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z")

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
    "net.kyori:adventure-platform-bukkit:4.3.1",
    "net.kyori:adventure-text-minimessage:4.14.0",
//    "net.kyori:adventure-text-serializer-bungeecord:4.3.1",
)

/**
 * 要合并的文件列表
 */
val combineFiles = arrayOf(
    "config.yml",
    "lang/zh_CN.yml",
    "lang/en_US.yml"
)