package cn.fd.ratziel.item.impl

import cn.fd.ratziel.bukkit.util.nbt.NBTTag
import cn.fd.ratziel.core.util.quickFuture
import cn.fd.ratziel.item.api.ItemInfo
import cn.fd.ratziel.item.api.builder.ItemSerializer
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
 * DefaultItemSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/28 13:26
 */
// TODO 完善This
open class DefaultItemSerializer(
    /**
     * 物品ID
     */
    val itemID:String,
    /**
     * 物品元数据序列化器
     */
    val metaSerializer: ItemMetadataSerializer = ItemMetadataSerializer(),
) : ItemSerializer {

    override fun serializeByJson(json: Json, element: JsonElement): RazItem {
        val serialized = metaSerializer.serializeByJson(json, element)
        return RazItem(ItemInfo(itemID, NBTTag()),serialized)
    }
}

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
        return VItemMeta(display.get(), characteristic.get(), durability.get(), nbt.get() ?: ItemTag())
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
        } ?: ItemTag()
}