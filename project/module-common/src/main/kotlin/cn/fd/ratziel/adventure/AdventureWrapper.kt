package cn.fd.ratziel.adventure

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.AMPERSAND_CHAR
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.SECTION_CHAR
import taboolib.module.nms.MinecraftVersion

/**
 * Gson序列化器
 */
private val gsonComponentSerializer by lazy {
    // 1.16+
    if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_16)) {
        GsonComponentSerializer.gson()
    } else GsonComponentSerializer.colorDownsamplingGson()
}

/**
 * 将组件转换成Json
 */
fun Component.toJsonFormat() = gsonComponentSerializer.serialize(this)

/**
 * 将Json转化成组件
 */
fun jsonToComponent(json: String) = gsonComponentSerializer.deserialize(json)

/**
 * MiniMessage
 */
fun deserializeByMiniMessage(target: String)
        : Component = MiniMessage.miniMessage().deserialize(target)

fun serializeByMiniMessage(target: Component)
        : String = MiniMessage.miniMessage().serialize(target)

/**
 * 将 '&' 转换成 '§'
 */
fun translateAmpersandColor(target: String) = target.replace(AMPERSAND_CHAR, SECTION_CHAR)

/**
 * 将 '§' 转换成 '&'
 */
fun translateLegacyColor(target: String) = target.replace(SECTION_CHAR, AMPERSAND_CHAR)

/**
 * 旧版消息格式序列化器
 */
private val legacyComponentSerializer by lazy {
    LegacyComponentSerializer.builder().apply {
        character(SECTION_CHAR)
        // 1.16+
        if (MinecraftVersion.majorLegacy >= 11600) {
            hexColors()
            useUnusualXRepeatedCharacterHexFormat()
        }
    }.build()
}

fun serializeLegacy(target: Component): String = legacyComponentSerializer.serialize(target)

fun deserializeLegacy(target: String): TextComponent = legacyComponentSerializer.deserialize(target)