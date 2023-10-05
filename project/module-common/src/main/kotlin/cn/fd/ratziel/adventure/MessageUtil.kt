package cn.fd.ratziel.adventure

import net.kyori.adventure.text.Component
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components

// 换行符
const val NEWLINE_SIGN = "\n"

/**
 * 基本消息构建
 */
fun buildMessage(target: String?): Component = parseAdventure(target)

/**
 * Taboolib消息解析
 */
fun parseTaboolibMessage(target: String?): ComponentText =
    target?.let { Components.parseSimple(it).build() } ?: ComponentText.empty()

/**
 * 冒险API消息解析
 */
fun parseAdventure(target: String?): Component =
    target?.let {
        deserializeByMiniMessage(
            serializeByMiniMessage(
                deserializeLegacy(translateAmpersandColor(it))
            ).replace("\\<", "<")
                .replace("\\>", ">")
        )
    } ?: Component.empty()

/**
 * 将组件列表进行格式化
 * 根据换行符重新构建列表
 */
//fun List<Component>.format() :List<Component> = this.flatMap {
//    // 转化为 JsonElement
//    val json = baseJson.parseToJsonElement(it.toJsonFormat())
//    if(json is JsonObject)
//    }