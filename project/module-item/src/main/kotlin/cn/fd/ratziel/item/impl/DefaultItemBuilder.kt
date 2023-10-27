package cn.fd.ratziel.item.impl

import cn.fd.ratziel.core.util.quickFuture
import cn.fd.ratziel.item.meta.VItemCharacteristic
import cn.fd.ratziel.item.meta.VItemDisplay
import cn.fd.ratziel.item.meta.VItemDurability
import cn.fd.ratziel.item.meta.VItemMeta
import cn.fd.ratziel.item.util.NBTMapper
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import taboolib.module.nms.ItemTag
import java.util.concurrent.CompletableFuture

/**
 * DefaultItemBuilder
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:34
 */
class DefaultItemBuilder : ItemBuilder() {

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
                    ?.let { NBTMapper.mapFromJson(it) }
            } catch (_: IllegalArgumentException) {
                null
            }
        }
        return VItemMeta(display.get(), characteristic.get(), durability.get(), nbt.get() ?: ItemTag())
    }

}