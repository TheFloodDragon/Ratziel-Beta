const val rootName = "Ratziel"
const val rootGroup = "cn.fd.ratziel"
const val rootVersion = "A.0.2.5.0"

const val kotlinVersion = "2.0.0-Beta3"
const val serializationVersion = "1.6.2"
const val coroutineVersion = "1.7.3"
const val shadowJarVersion = "8.1.1"
const val taboolibPluginVersion = "2.0.0"

val taboolibVersion = getLatestRelease("TabooLib", "taboolib").also { println("Using taboolib-version = $it") }

val isoInstantFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z")

val adventureModules = setOf(
    "net.kyori:adventure-api:4.15.0",
    "net.kyori:adventure-platform-bukkit:4.3.2",
    "net.kyori:adventure-text-minimessage:4.15.0"
)

/**
 * 要合并的文件列表
 */
val combineFiles = arrayOf(
    "config.yml",
    "lang/zh_CN.yml",
    "lang/en_US.yml"
)

val taboolibModules = setOf(
    // 核心模块
    "common", "common-env", "common-util", "common-legacy-api", "common-platform-api", "common-reflex",
    // 泛用模块
    "module-chat", "module-configuration", "module-lang", "expansion-command-helper"
)