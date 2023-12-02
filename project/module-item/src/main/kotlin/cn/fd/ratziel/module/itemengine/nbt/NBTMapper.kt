@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.serialization.adapt
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import taboolib.module.nms.ItemTagSerializer

/**
 * NBTMapper - 将Json映射成TiNBTTag
 *
 * @author TheFloodDragon
 * @since 2023/10/15 9:08
 */
@Serializer(TiNBTTag::class)
object NBTMapper : KSerializer<TiNBTTag> {

    override fun deserialize(decoder: Decoder): TiNBTTag = mapFromJson((decoder as JsonDecoder).decodeJsonElement())

    override fun serialize(encoder: Encoder, value: TiNBTTag) =
        (encoder as JsonEncoder).encodeJsonElement(encoder.json.parseToJsonElement(value.toJson()))

    @JvmStatic
    fun mapFromJson(json: JsonElement, nbtTag: TiNBTTag = TiNBTTag()): TiNBTTag = nbtTag.also { tag ->
        when (json) {
            is JsonObject -> json.forEach { key, value ->
                tag.putDeep(key, mapFromJson(value, tag))
            }

            is JsonArray -> TiNBTData.translateList(TiNBTList(), json.map { mapFromJson(it, tag) })
            is JsonPrimitive -> deserializePrimitive(json)
        }
    }


    fun deserializePrimitive(json: JsonPrimitive): TiNBTData = try {
        ItemTagSerializer.deserializeData(com.google.gson.JsonParser.parseString(json.toString()))
    } catch (_: IllegalStateException) {
        TiNBTData.toNBT(json.adapt())
    }

}