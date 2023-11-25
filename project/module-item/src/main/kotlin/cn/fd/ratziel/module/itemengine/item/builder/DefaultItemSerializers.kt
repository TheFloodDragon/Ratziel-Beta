package cn.fd.ratziel.module.itemengine.item.builder

import cn.fd.ratziel.core.function.quickFuture
import cn.fd.ratziel.module.itemengine.api.builder.ItemSerializer
import cn.fd.ratziel.module.itemengine.item.meta.VItemCharacteristic
import cn.fd.ratziel.module.itemengine.item.meta.VItemDisplay
import cn.fd.ratziel.module.itemengine.item.meta.VItemDurability
import cn.fd.ratziel.module.itemengine.item.meta.VItemMeta
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import cn.fd.ratziel.module.itemengine.nbt.TiNBTTag
import cn.fd.ratziel.module.itemengine.util.NBTSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import java.util.concurrent.CompletableFuture

/**
 * ItemMetadata
 */
open class ItemMetadataSerializer(
    /**
     * 各部分序列化器
     */
    val displaySerializer: ItemDisplaySerializer = ItemDisplaySerializer(),
    val charSerializer: ItemCharSerializer = ItemCharSerializer(),
    val durabilitySerializer: ItemDurabilitySerializer = ItemDurabilitySerializer(),
    val itemTagSerializer: TiNBTTagSerializer = TiNBTTagSerializer(),
) : ItemSerializer {
    override fun serializeByJson(json: Json, element: JsonElement): VItemMeta {
        val display = quickFuture { displaySerializer.serializeByJson(json, element) }
        val characteristic = quickFuture { charSerializer.serializeByJson(json, element) }
        val durability = quickFuture { durabilitySerializer.serializeByJson(json, element) }
        val nbt: CompletableFuture<TiNBTTag?> = quickFuture { itemTagSerializer.serializeByJson(element) }
        return VItemMeta(display.get(), characteristic.get(), durability.get(), NBTTag.of(nbt.get() ?: TiNBTTag()))
    }
}


/**
 * ItemDisplay
 */
open class ItemDisplaySerializer : ItemSerializer {
    override fun serializeByJson(json: Json, element: JsonElement) =
        json.decodeFromJsonElement<VItemDisplay>(element)
}

/**
 * ItemCharacteristic
 */
open class ItemCharSerializer : ItemSerializer {
    override fun serializeByJson(json: Json, element: JsonElement) =
        json.decodeFromJsonElement<VItemCharacteristic>(element)
}

/**
 * ItemDurability
 */
open class ItemDurabilitySerializer : ItemSerializer {
    override fun serializeByJson(json: Json, element: JsonElement) =
        json.decodeFromJsonElement<VItemDurability>(element)
}

/**
 * TiNBTTag
 */
open class TiNBTTagSerializer {
    fun serializeByJson(element: JsonElement, source: TiNBTTag = TiNBTTag()) =
        try {
            (element.jsonObject["nbt"]
                ?: element.jsonObject["itemTag"]
                ?: element.jsonObject["itemTags"])
                ?.let { NBTSerializer.mapFromJson(it, source) }
        } catch (_: IllegalArgumentException) {
            null
        }
}