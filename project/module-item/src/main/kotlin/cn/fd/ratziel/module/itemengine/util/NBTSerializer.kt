@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.util

import cn.fd.ratziel.core.serialization.adapt
import cn.fd.ratziel.module.itemengine.nbt.NBTData
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
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
@Serializer(NBTTag::class)
object NBTSerializer : KSerializer<NBTTag> {

    override fun deserialize(decoder: Decoder): NBTTag =
        mapFromJson((decoder as JsonDecoder).decodeJsonElement(), NBTTag())

    override fun serialize(encoder: Encoder, value: NBTTag) =
        (encoder as JsonEncoder).encodeJsonElement(encoder.json.parseToJsonElement(value.toJson()))

    @JvmStatic
    fun mapFromJson(json: JsonElement, nbtTag: NBTTag = NBTTag()): NBTTag {
        fun transform(json: JsonElement): NBTData? = when (json) {
            is JsonPrimitive -> NBTData.toNBT(json.adapt())
            is JsonArray -> NBTData.toNBT(json.mapNotNull { transform(it) })
            is JsonObject -> mapFromJson(json)
            else -> null
        }
        if (json is JsonObject) json.forEach { key, value ->
            transform(value)?.also { nbtTag.putDeep(key, it) }
        }
        return nbtTag
    }

}