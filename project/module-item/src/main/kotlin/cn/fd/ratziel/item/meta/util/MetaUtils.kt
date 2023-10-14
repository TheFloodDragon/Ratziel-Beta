package cn.fd.ratziel.item.meta.util

import cn.fd.ratziel.adventure.serializeByMiniMessage
import cn.fd.ratziel.adventure.toJsonFormat
import cn.fd.ratziel.item.api.ItemCharacteristic
import cn.fd.ratziel.item.meta.VItemCharacteristic
import net.kyori.adventure.text.Component
import taboolib.module.nms.MinecraftVersion

typealias ItemChar = ItemCharacteristic
typealias VItemChar = VItemCharacteristic

/**
 * NMS:
 *   1.13+ > Json Format
 *   1.13- > Original Format (§)
 */
fun nmsComponent(component: Component): String =
    if (MinecraftVersion.isLower(MinecraftVersion.V1_13)) {
        serializeByMiniMessage(component)
    } else component.toJsonFormat()