package cn.fd.ratziel.adventure

import net.kyori.adventure.text.Component

/**
 * 基本消息构建
 */
fun buildMessage(target: String) =
    deserializeByMiniMessage(
        serializeByMiniMessage(
            deserializeLegacy(translateAmpersandColor(target))
        ).replace("\\<", "<")
            .replace("\\>", ">")
    )

@JvmName("buildMessageNullable")
fun buildMessage(target: String?) =
    target?.let { buildMessage(it) } ?: Component.empty()