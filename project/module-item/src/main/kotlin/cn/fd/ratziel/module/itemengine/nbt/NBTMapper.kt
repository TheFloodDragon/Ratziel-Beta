@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.util.adapt
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

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
        deserializeBasic(if (json.isString) json.content else json.content.adapt())

    const val SPECIAL_TYPE_SIGN = ":"
    val NBT_COMPOUND_SIGNS = arrayOf(":c", ":compound", ":cpd")
    val NBT_LIST_SIGNS = arrayOf(":l", ":list")

    /**
     * 对基本类型的反序列处理
     * 处理方式:
     * 1. [SPECIAL_TYPE_SIGN] 开头时,使用 [deserializePrimitive] 解析
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
                lowValue.startsWith(SPECIAL_TYPE_SIGN) -> deserializePrimitive(value.drop(SPECIAL_TYPE_SIGN.length))
                else -> toNBTData(value.adapt())
            }
        } else return toNBTData(value)
    }

    /**
     * 将 [NBTData] 序列化成 [String]
     */
    fun serializeToString(value: NBTData) = SPECIAL_TYPE_SIGN + serializePrimitive(value)

    const val PRIMITIVE_ARRAY_SEPARATOR = ","
    const val PRIMITIVE_ARRAY_BYTE = "ba"
    const val PRIMITIVE_ARRAY_INT = "ia"
    const val PRIMITIVE_ARRAY_LONG = "la"
    const val PRIMITIVE_BYTE = "b"
    const val PRIMITIVE_INT = "i"
    const val PRIMITIVE_LONG = "l"
    const val PRIMITIVE_SHORT = "s"
    const val PRIMITIVE_FLOAT = "f"
    const val PRIMITIVE_DOUBLE = "d"
    const val PRIMITIVE_STRING = "T"

    /**
     * 精确反序列化基本类型 (除了[NBTCompound]和[NBTList]的[NBTData])
     */
    fun deserializePrimitive(prim: String, ignoreCase: Boolean = false): NBTData = when {
        // Array
        prim.endsWith(PRIMITIVE_ARRAY_BYTE, ignoreCase) ->
            prim.dropLast(PRIMITIVE_ARRAY_BYTE.length).split(PRIMITIVE_ARRAY_SEPARATOR)
                .mapNotNull { it.toByteOrNull() }.toByteArray().let { NBTByteArray(it) }

        prim.endsWith(PRIMITIVE_ARRAY_INT, ignoreCase) ->
            prim.dropLast(PRIMITIVE_ARRAY_INT.length).split(PRIMITIVE_ARRAY_SEPARATOR)
                .mapNotNull { it.toIntOrNull() }.toIntArray().let { NBTIntArray(it) }

        prim.endsWith(PRIMITIVE_ARRAY_LONG, ignoreCase) ->
            prim.dropLast(PRIMITIVE_ARRAY_LONG.length).split(PRIMITIVE_ARRAY_SEPARATOR)
                .mapNotNull { it.toLongOrNull() }.toLongArray().let { NBTLongArray(it) }
        // Basic
        prim.endsWith(PRIMITIVE_BYTE, ignoreCase) -> NBTByte(prim.dropLast(PRIMITIVE_BYTE.length).toByte())
        prim.endsWith(PRIMITIVE_SHORT, ignoreCase) -> NBTShort(prim.dropLast(PRIMITIVE_SHORT.length).toShort())
        prim.endsWith(PRIMITIVE_INT, ignoreCase) -> NBTInt(prim.dropLast(PRIMITIVE_INT.length).toInt())
        prim.endsWith(PRIMITIVE_LONG, ignoreCase) -> NBTLong(prim.dropLast(PRIMITIVE_LONG.length).toLong())
        prim.endsWith(PRIMITIVE_FLOAT, ignoreCase) -> NBTFloat(prim.dropLast(PRIMITIVE_FLOAT.length).toFloat())
        prim.endsWith(PRIMITIVE_DOUBLE, ignoreCase) -> NBTDouble(prim.dropLast(PRIMITIVE_DOUBLE.length).toDouble())
        prim.endsWith(PRIMITIVE_STRING, ignoreCase) -> NBTString(prim.dropLast(PRIMITIVE_STRING.length))
        else -> toNBTData(prim.adapt())
    }

    /**
     * 精确序列化基本类型 (除了[NBTCompound]和[NBTList]的[NBTData])
     */
    fun serializePrimitive(data: NBTData) = when (data) {
        // Basic
        is NBTByte -> data.toString().toByte().toString() + PRIMITIVE_BYTE
        is NBTShort -> data.toString().toShort().toString() + PRIMITIVE_SHORT
        is NBTInt -> data.toString().toInt().toString() + PRIMITIVE_INT
        is NBTLong -> data.toString().toLong().toString() + PRIMITIVE_LONG
        is NBTFloat -> data.toString().toFloat().toString() + PRIMITIVE_FLOAT
        is NBTDouble -> data.toString().toDouble().toString() + PRIMITIVE_DOUBLE
        is NBTString -> data.content + PRIMITIVE_STRING
        // Array (Translate to TiNBTData)
        is NBTIntArray -> data.getAsTiNBT().asIntArray()
            .joinToString(PRIMITIVE_ARRAY_SEPARATOR) { it.toString() } + PRIMITIVE_ARRAY_INT

        is NBTByteArray -> data.getAsTiNBT().asByteArray()
            .joinToString(PRIMITIVE_ARRAY_SEPARATOR) { it.toString() } + PRIMITIVE_ARRAY_BYTE

        is NBTLongArray -> data.getAsTiNBT().asLongArray()
            .joinToString(PRIMITIVE_ARRAY_SEPARATOR) { it.toString() } + PRIMITIVE_ARRAY_LONG

        else -> null
    }

}