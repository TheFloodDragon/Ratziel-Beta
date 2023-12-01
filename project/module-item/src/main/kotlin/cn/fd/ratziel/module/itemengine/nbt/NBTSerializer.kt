@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.serialization.adapt
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * NBTSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/15 9:08
 */
@Serializer(TiNBTTag::class)
@Deprecated("待改进")
object NBTSerializer : KSerializer<TiNBTTag> {

    override fun deserialize(decoder: Decoder): TiNBTTag =
        mapFromJson((decoder as JsonDecoder).decodeJsonElement(), TiNBTTag())

    override fun serialize(encoder: Encoder, value: TiNBTTag) =
        (encoder as JsonEncoder).encodeJsonElement(encoder.json.parseToJsonElement(value.toJson()))

    @JvmStatic
    fun mapFromJson(json: JsonElement, nbtTag: TiNBTTag = TiNBTTag()): TiNBTTag {
        fun transform(json: JsonElement): TiNBTData? = when (json) {
            is JsonPrimitive -> TiNBTData.toNBT(json.adapt())
            is JsonArray -> TiNBTData.toNBT(json.mapNotNull { transform(it) })
            is JsonObject -> mapFromJson(json)
            else -> null
        }
        if (json is JsonObject) json.forEach { key, value ->
            transform(value)?.also { nbtTag.putDeep(key, it) }
        }
        return nbtTag
    }

}