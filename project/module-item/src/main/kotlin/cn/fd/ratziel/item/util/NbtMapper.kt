package cn.fd.ratziel.item.util

import kotlinx.serialization.json.*
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData

/**
 * NbtMapper
 *
 * @author TheFloodDragon
 * @since 2023/10/15 9:08
 */
object NbtMapper {

    @JvmStatic
    fun mapFromJson(json: JsonElement, itemTag: ItemTag = ItemTag()): ItemTag {
        fun translate(json: JsonElement): ItemTagData? =
            when (json) {
                is JsonPrimitive -> ItemTagData.toNBT(json.contentOrNull)
                is JsonArray -> ItemTagData.toNBT(json.map { translate(it) })
                is JsonObject -> mapFromJson(json)
                else -> null
            }
        (json as? JsonObject)?.forEach { key, value ->
            itemTag.putDeep(key, translate(value))
        }
        return itemTag
    }

}