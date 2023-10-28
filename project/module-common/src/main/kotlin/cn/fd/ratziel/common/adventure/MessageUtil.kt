package cn.fd.ratziel.common.adventure

import cn.fd.ratziel.core.serialization.isJson
import net.kyori.adventure.text.Component
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components

/**
 * 基本消息构建
 */
fun buildMessage(source: String?): Component = source?.let { parseAdventure(it) } ?: Component.empty()

/**
 * 可能是Json字符串的消息构建
 */
fun buildMessageMJ(source: String?): Component = source?.let {
    if (it.isJson()) jsonToComponent(it)
    else buildMessage(it)
} ?: Component.empty()

/**
 * Taboolib消息解析
 */
fun parseTaboolibMessage(source: String?): ComponentText =
    source?.let { Components.parseSimple(it).build() } ?: ComponentText.empty()

/**
 * 冒险API消息解析
 */
fun parseAdventure(source: String): Component =
    deserializeByMiniMessage(
        serializeByMiniMessage(
            deserializeLegacy(translateAmpersandColor(source))
        ).replace("\\<", "<")
            .replace("\\>", ">")
    )