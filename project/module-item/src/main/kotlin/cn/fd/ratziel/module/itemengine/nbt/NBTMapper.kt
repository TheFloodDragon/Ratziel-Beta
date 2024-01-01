@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.serialization.adapt
import cn.fd.ratziel.core.util.adapt
import cn.fd.ratziel.core.util.endsWithNonEscaped
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import taboolib.module.nms.ItemTagSerializer

/**
 * NBTMapper - 将Json映射成NBTTag
 *
 * @author TheFloodDragon
 * @since 2023/10/15 9:08
 */
@Serializer(TiNBTTag::class)
object NBTMapper : KSerializer<TiNBTTag> {

    const val SPECIAL_TYPE_SIGN = ";"

    override fun deserialize(decoder: Decoder): TiNBTTag =
        mapFromJson((decoder as JsonDecoder).decodeJsonElement()).getAsTiNBT()

    override fun serialize(encoder: Encoder, value: TiNBTTag) =
        (encoder as JsonEncoder).encodeJsonElement(encoder.json.parseToJsonElement(value.toJson()))

    @JvmStatic
    fun mapFromJson(json: JsonElement, source: NBTTag = NBTTag()): NBTTag = deserialize(json, source) as NBTTag

    /**
     * 将 [json] 反序列化成 [NBTData]
     */
    fun deserialize(json: JsonElement, source: NBTTag = NBTTag()): NBTData =
        when (json) {
            is JsonPrimitive -> deserializePrimitive(json)
            is JsonArray -> NBTList.of(json.map { deserialize(it) })
            is JsonObject -> source.also { tag ->
                json.forEach {
                    val newSource = tag.getDeep(it.key) as? NBTTag ?: NBTTag()
                    tag.putDeep(it.key, deserialize(it.value, newSource))
                }
            }
        }

    fun deserializePrimitive(json: JsonPrimitive): NBTData =
        deserializeBasic(if (json.isString) json.content else json.adapt())

    /**
     * 对基本类型的反序列处理
     * 处理方式:
     * 当末尾有 [SPECIAL_TYPE_SIGN] 时,使用 [taboolib.module.nms.ItemTagSerializer] 解析
     * 反之则尝试适应性解析,再不济就直接转化了
     */
    fun deserializeBasic(value: Any): NBTData = toNBTData(
        if (value is String) {
            val check = value.endsWithNonEscaped(SPECIAL_TYPE_SIGN)
            if (check.second)
                ItemTagSerializer.deserializeData(com.google.gson.JsonPrimitive(check.first.dropLast(SPECIAL_TYPE_SIGN.length)))
            else check.first.adapt()
        } else value
    )

    /**
     * 将 [TiNBTData] 序列化成 [JsonElement]
     */
    fun serializeToJson(value: TiNBTData) = ItemTagSerializer.serializeData(value)

    fun serializeToString(value: TiNBTData) = serializeToJson(value).asString + SPECIAL_TYPE_SIGN

}
