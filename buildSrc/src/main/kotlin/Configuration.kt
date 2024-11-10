const val rootName = "Ratziel"
const val rootGroup = "cn.fd.ratziel"
const val rootVersion = "A.0.3.1.2"

const val kotlinVersion = "2.1.0-RC"
const val serializationVersion = "1.7.2"
const val coroutineVersion = "1.9.0"
const val shadowJarVersion = "8.3.5"
const val taboolibPluginVersion = "2.0.21"

const val taboolibVersion = "6.2.0-beta33"

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