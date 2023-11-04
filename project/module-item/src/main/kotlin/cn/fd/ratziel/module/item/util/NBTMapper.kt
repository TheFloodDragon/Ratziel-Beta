@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.core.serialization.adapt
import cn.fd.ratziel.module.item.nbt.NBTData
import cn.fd.ratziel.module.item.nbt.NBTTag
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * NBTMapper
 *
 * @author TheFloodDragon
 * @since 2023/10/15 9:08
 */
@Serializer(NBTTag::class)
object NBTMapper : KSerializer<NBTTag> {

    override fun deserialize(decoder: Decoder): NBTTag =
        mapFromJson((decoder as JsonDecoder).decodeJsonElement(), NBTTag())

    override fun serialize(encoder: Encoder, value: NBTTag) =
        (encoder as JsonEncoder).encodeJsonElement(encoder.json.parseToJsonElement(value.toJson()))

    @JvmStatic
    fun mapFromJson(json: JsonElement, nbtTag: NBTTag = NBTTag()): NBTTag {
        fun translate(json: JsonElement): NBTData? =
            when (json) {
            is JsonPrimitive -> NBTData.toNBT(json.adapt())
                is JsonArray -> NBTData.toNBT(json.map { translate(it) })
                is JsonObject -> mapFromJson(json)
                else -> null
            }
        (json as? JsonObject)?.forEach { key, value ->
            nbtTag.putDeep(key, translate(value))
        }
        return nbtTag
    }

}