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

    const val SPECIAL_TYPE_SIGN = ";"

    override fun deserialize(decoder: Decoder): TiNBTTag = mapFromJson((decoder as JsonDecoder).decodeJsonElement())

    override fun serialize(encoder: Encoder, value: TiNBTTag) =
        (encoder as JsonEncoder).encodeJsonElement(encoder.json.parseToJsonElement(value.toJson()))

    fun mapFromJson(json: JsonElement, nbtTag: TiNBTTag = TiNBTTag()): TiNBTTag = nbtTag.also { tag ->
        when (json) {
            is JsonPrimitive -> deserializePrimitive(json)
            is JsonArray -> TiNBTData.translateList(TiNBTList(), json.map { mapFromJson(it, tag) })
            is JsonObject -> json.forEach {
                tag.putDeep(it.key, mapFromJson(it.value, tag))
            }
        }
    }

    fun deserializePrimitive(json: JsonPrimitive): TiNBTData =
        // 当末尾有 ';' 时,使用 Taboolib 的 ItemTagSerializer 解析
        if (json.isString && json.content.endsWith(SPECIAL_TYPE_SIGN) && !json.content.endsWith('\\'+SPECIAL_TYPE_SIGN))
            ItemTagSerializer.deserializeData(com.google.gson.JsonPrimitive(json.content.dropLast(1)))
        else TiNBTData.toNBT(json.adapt()) // 正常解析

}