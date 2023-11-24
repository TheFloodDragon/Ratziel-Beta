package cn.fd.ratziel.module.itemengine.util

import cn.fd.ratziel.common.adventure.serializeLegacy
import cn.fd.ratziel.common.adventure.toJsonString
import cn.fd.ratziel.module.itemengine.api.meta.ItemCharacteristic
import cn.fd.ratziel.module.itemengine.item.meta.VItemCharacteristic
import net.kyori.adventure.text.Component
import taboolib.module.nms.MinecraftVersion

typealias ItemChar = ItemCharacteristic
typealias VItemChar = VItemCharacteristic

/**
 * NMS:
 *   1.13+ > Json Format
 *   1.13- > Original Format (§)
 */
fun nmsComponent(component: Component?): String? =
    if (MinecraftVersion.isLower(MinecraftVersion.V1_13)) {
        component?.let { serializeLegacy(it) }
    } else component?.toJsonString()