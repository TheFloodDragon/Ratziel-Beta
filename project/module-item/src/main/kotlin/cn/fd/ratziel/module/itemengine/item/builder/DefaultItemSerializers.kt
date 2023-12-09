package cn.fd.ratziel.module.itemengine.item.builder

import cn.fd.ratziel.core.function.futureAsync
import cn.fd.ratziel.module.itemengine.api.builder.ItemSerializer
import cn.fd.ratziel.module.itemengine.item.meta.VItemCharacteristic
import cn.fd.ratziel.module.itemengine.item.meta.VItemDisplay
import cn.fd.ratziel.module.itemengine.item.meta.VItemDurability
import cn.fd.ratziel.module.itemengine.item.meta.VItemMeta
import cn.fd.ratziel.module.itemengine.nbt.NBTMapper
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import cn.fd.ratziel.module.itemengine.nbt.TiNBTTag
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
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
    val itemTagSerializer: NBTTagSerializer = NBTTagSerializer(),
) : ItemSerializer {
    override fun serializeByJson(json: Json, element: JsonElement): VItemMeta {
        val display = futureAsync { displaySerializer.serializeByJson(json, element) }
        val characteristic = futureAsync { charSerializer.serializeByJson(json, element) }
        val durability = futureAsync { durabilitySerializer.serializeByJson(json, element) }
        val nbt: CompletableFuture<NBTTag?> = futureAsync { itemTagSerializer.serializeByJson(element) }
        return VItemMeta(display.get(), characteristic.get(), durability.get(), nbt.get() ?: NBTTag())
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
 * NBTTag
 */
open class NBTTagSerializer {
    fun serializeByJson(element: JsonElement, source: NBTTag = NBTTag()): NBTTag? =
        (element.takeIf { element is JsonObject } as? JsonObject)?.let { o ->
            val json = o["nbt"] ?: o["itemTag"] ?: o["itemTags"]
            json?.let {
                NBTMapper.mapFromJson(it, source.getAsTiNBT() as TiNBTTag).let { t -> NBTTag.of(t) }
            }
        }
}