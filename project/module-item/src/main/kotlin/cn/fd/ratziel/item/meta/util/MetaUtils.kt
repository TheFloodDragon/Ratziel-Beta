package cn.fd.ratziel.item.meta.util

import cn.fd.ratziel.adventure.serializeByMiniMessage
import cn.fd.ratziel.adventure.toJsonFormat
import cn.fd.ratziel.core.util.quickFuture
import cn.fd.ratziel.item.api.ItemCharacteristic
import cn.fd.ratziel.item.meta.VItemCharacteristic
import cn.fd.ratziel.item.meta.VItemDisplay
import cn.fd.ratziel.item.meta.VItemMeta
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
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

/**
 * 快速构建VItemMeta
 */
fun buildVMeta(json: Json, element: JsonElement): VItemMeta {
    val display = quickFuture { json.decodeFromJsonElement<VItemDisplay>(element) }
    val char = quickFuture { json.decodeFromJsonElement<VItemChar>(element) }
    return VItemMeta(display.get(), char.get())
}