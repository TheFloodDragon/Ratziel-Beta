package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.core.util.quickFuture
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.item.meta.VItemCharacteristic
import cn.fd.ratziel.module.item.item.meta.VItemDisplay
import cn.fd.ratziel.module.item.item.meta.VItemDurability
import cn.fd.ratziel.module.item.item.meta.VItemMeta
import cn.fd.ratziel.module.item.util.NBTMapper
import cn.fd.ratziel.module.item.util.emptyTag
import kotlinx.serialization.json.*
import taboolib.module.nms.ItemTag
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
    val itemTagSerializer: ItemNBTTagSerializer = ItemNBTTagSerializer(),
) : ItemSerializer {
    override fun serializeByJson(json: Json, element: JsonElement): VItemMeta {
        val display = quickFuture { displaySerializer.serializeByJson(json, element) }
        val characteristic = quickFuture { charSerializer.serializeByJson(json, element) }
        val durability = quickFuture { durabilitySerializer.serializeByJson(json, element) }
        val nbt: CompletableFuture<ItemTag> = quickFuture { itemTagSerializer.serializeByJson(element) }
        return VItemMeta(display.get(), characteristic.get(), durability.get(), nbt.get() ?: emptyTag())
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
 * ItemTag
 */
open class ItemNBTTagSerializer {
    fun serializeByJson(element: JsonElement) =
        try {
            (element.jsonObject["nbt"]
                ?: element.jsonObject["itemTag"]
                ?: element.jsonObject["itemTags"])
                ?.let { NBTMapper.mapFromJson(it) }
        } catch (_: IllegalArgumentException) {
            null
        } ?: emptyTag()
}