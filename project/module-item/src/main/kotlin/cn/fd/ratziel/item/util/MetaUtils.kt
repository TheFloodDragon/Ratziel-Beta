package cn.fd.ratziel.item.util

import cn.fd.ratziel.adventure.serializeByMiniMessage
import cn.fd.ratziel.adventure.toJsonFormat
import cn.fd.ratziel.core.util.quickFuture
import cn.fd.ratziel.item.api.ItemCharacteristic
import cn.fd.ratziel.item.meta.VItemCharacteristic
import cn.fd.ratziel.item.meta.VItemDisplay
import cn.fd.ratziel.item.meta.VItemDurability
import cn.fd.ratziel.item.meta.VItemMeta
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import net.kyori.adventure.text.Component
import taboolib.module.nms.ItemTag
import taboolib.module.nms.MinecraftVersion
import java.util.concurrent.CompletableFuture

typealias ItemChar = ItemCharacteristic
typealias VItemChar = VItemCharacteristic

/**
 * NMS:
 *   1.13+ > Json Format
 *   1.13- > Original Format (§)
 */
fun nmsComponent(component: Component?): String? =
    if (MinecraftVersion.isLower(MinecraftVersion.V1_13)) {
        component?.let { serializeByMiniMessage(it) }
    } else component?.toJsonFormat()

/**
 * 构建 VItemMeta
 */
fun buildVMeta(json: Json, element: JsonElement): VItemMeta {
    val display = quickFuture { json.decodeFromJsonElement<VItemDisplay>(element) }
    val characteristic = quickFuture { json.decodeFromJsonElement<VItemCharacteristic>(element) }
    val durability = quickFuture { json.decodeFromJsonElement<VItemDurability>(element) }
    val nbt: CompletableFuture<ItemTag?> = quickFuture {
        try {
            (element.jsonObject["nbt"]
                ?: element.jsonObject["itemTag"]
                ?: element.jsonObject["itemTags"])
                ?.let { NbtMapper.mapFromJson(it) }
        } catch (_: IllegalArgumentException) {
            null
        }
    }
    return VItemMeta(display.get(), characteristic.get(), durability.get(), nbt.get() ?: ItemTag())
}