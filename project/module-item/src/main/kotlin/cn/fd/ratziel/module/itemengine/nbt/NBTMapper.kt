@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.serialization.adapt
import cn.fd.ratziel.core.util.adapt
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

    const val SPECIAL_TYPE_SIGN = ":"
    val NBT_COMPOUND_SIGNS = arrayOf(":c", ":compound", ":cpd")
    val NBT_LIST_SIGNS = arrayOf(":l", ":list")

    /**
     * 对基本类型的反序列处理
     * 处理方式:
     * 1. [SPECIAL_TYPE_SIGN] 开头时,使用 [taboolib.module.nms.ItemTagSerializer] 解析
     * 2. [NBT_COMPOUND_SIGNS] 代表空的 [NBTCompound]
     * 3. [NBT_LIST_SIGNS] 代表空的 [NBTList]
     * 4. 直接尝试适应性转化
     */
    fun deserializeBasic(value: Any): NBTData {
        if (value is String) {
            val lowValue = value.lowercase() // 忽略大小写
            return when {
                NBT_COMPOUND_SIGNS.contains(lowValue) -> NBTCompound()
                NBT_LIST_SIGNS.contains(lowValue) -> NBTList()
                lowValue.startsWith(SPECIAL_TYPE_SIGN) -> NBTConverter.TiConverter.convert(
                    ItemTagSerializer.deserializeData(com.google.gson.JsonPrimitive(value.drop(SPECIAL_TYPE_SIGN.length)))
                )
                else -> toNBTData(value.adapt())
            }
        } else return toNBTData(value)
    }

    /**
     * 将 [TiNBTData] 序列化成 [JsonElement]
     */
    fun serializeToJson(value: TiNBTData) = ItemTagSerializer.serializeData(value)

    fun serializeToString(value: TiNBTData) = serializeToJson(value).asString + SPECIAL_TYPE_SIGN

}