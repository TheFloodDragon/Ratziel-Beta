const val rootName = "Ratziel"
const val rootGroup = "cn.fd.ratziel"
const val rootVersion = "A.0.3.0.9"

const val kotlinVersion = "2.0.0"
const val serializationVersion = "1.7.1"
const val coroutineVersion = "1.8.1"
const val shadowJarVersion = "8.1.1"
const val taboolibPluginVersion = "2.0.11"

const val taboolibVersion = "6.2.0-beta5-dev"

val adventureModules = setOf(
    "net.kyori:adventure-api:4.17.0",
    "net.kyori:adventure-text-serializer-gson:4.17.0",
    "net.kyori:adventure-text-minimessage:4.17.0",
    "net.kyori:adventure-text-serializer-legacy:4.17.0",
    "net.kyori:adventure-platform-bukkit:4.3.3",
)

/**
 * 要合并的文件列表
 */
val combineFiles = setOf(
    "settings.yml",
    "lang/zh_CN.yml",
    "lang/en_US.yml"
)

val taboolibModules = setOf(
    // 核心模块
    "common", "common-env", "common-util", "common-legacy-api", "common-platform-api", "common-reflex",
    // 泛用模块
    "basic-configuration", "minecraft-chat", "minecraft-i18n", "minecraft-command-helper"
)