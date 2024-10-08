package cn.fd.ratziel.module.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.core.util.adapt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * NBTSerializer
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:40
 */
object NBTSerializer : KSerializer<NBTData> {

    override val descriptor = PrimitiveSerialDescriptor("nbt.NBTData", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: NBTData) {
        if (encoder is JsonEncoder)
            encoder.encodeJsonElement(Converter.serializeToJson(value))
        else throw UnsupportedTypeException(encoder)
    }

    override fun deserialize(decoder: Decoder): NBTData {
        if (decoder is JsonDecoder)
            return Converter.deserializeFromJson(decoder.decodeJsonElement())
        else throw UnsupportedTypeException(decoder)
    }

    object Converter {

        /**
         * 精确类型控制符
         */
        const val EXACT_TYPE_CHAR = ";"

        const val ELEMENT_SEPARATOR = ","

        /**
         * 将 [JsonElement] 反序列化成 [NBTData]
         */
        fun deserializeFromJson(json: JsonElement, source: NBTCompound = NBTCompound()): NBTData =
            when (json) {
                is JsonPrimitive -> deserializeFromString(json.content)
                is JsonArray -> NBTList.of(json.map { deserializeFromJson(it, NBTCompound()) })
                is JsonObject -> source.also { tag ->
                    json.forEach {
                        val newSource = tag.getDeep(it.key) as? NBTCompound ?: NBTCompound()
                        tag.putDeep(it.key, deserializeFromJson(it.value, newSource))
                    }
                }
            }

        /**
         * 将 [NBTData] 序列化成 [JsonElement]
         */
        fun serializeToJson(target: NBTData): JsonElement = when (target) {
            // 特殊类型序列化
            is NBTCompound -> serializeToJsonObject(target)
            is NBTList -> serializeToJsonArray(target)
            // 基础类型序列化
            else -> JsonPrimitive(serializeToString(target))
        }

        /**
         * 序列化精确类型
         */
        fun serializeToString(target: NBTData): String = when (target) {
            // Basic
            is NBTByte -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.BYTE.simpleName
            is NBTShort -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.SHORT.simpleName
            is NBTInt -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.INT.simpleName
            is NBTLong -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.LONG.simpleName
            is NBTFloat -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.FLOAT.simpleName
            is NBTDouble -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.DOUBLE.simpleName
            is NBTString -> target.content + EXACT_TYPE_CHAR + NBTType.STRING.simpleName
            // Array
            is NBTIntArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NBTType.INT_ARRAY.simpleName
            is NBTByteArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NBTType.BYTE_ARRAY.simpleName
            is NBTLongArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NBTType.LONG_ARRAY.simpleName
            // Special Type
            is NBTCompound -> serializeToJsonObject(target).toString() + EXACT_TYPE_CHAR + NBTType.COMPOUND.simpleName
            is NBTList -> serializeToJsonArray(target).toString() + EXACT_TYPE_CHAR + NBTType.LIST.simpleName
            else -> throw UnsupportedTypeException(target.type)
        }

        fun serializeToJsonObject(target: NBTCompound) = buildJsonObject { target.forEach { put(it.key, serializeToJson(it.value)) } }

        fun serializeToJsonArray(target: NBTList) = buildJsonArray { target.forEach { add(serializeToJson(it)) } }

        /**
         * 反序列化精确类型
         */
        fun deserializeFromString(target: String): NBTData {
            if (!target.contains(EXACT_TYPE_CHAR)) return adaptString(target)

            val typeStr = target.substringAfterLast(EXACT_TYPE_CHAR)
            val dataStr = target.substringBeforeLast(EXACT_TYPE_CHAR)

            // 匹配类型
            val type = NBTType.entries.find { it.names.contains(typeStr) } ?: return adaptString(target)

            return convertBasicString(dataStr, type)
                ?: convertArrayString(dataStr, type)
                ?: convertSpecialString(dataStr, type)
                ?: throw UnsupportedTypeException(type)
        }

        private fun adaptString(str: String) = NBTAdapter.box(str.adapt())

        private fun convertBasicString(str: String, type: NBTType) = when (type) {
            NBTType.BYTE -> NBTByte(str.toByte())
            NBTType.DOUBLE -> NBTDouble(str.toDouble())
            NBTType.SHORT -> NBTShort(str.toShort())
            NBTType.LONG -> NBTLong(str.toLong())
            NBTType.FLOAT -> NBTFloat(str.toFloat())
            NBTType.INT -> NBTInt(str.toInt())
            NBTType.STRING -> NBTString(str)
            else -> null
        }

        private fun convertArrayString(str: String, type: NBTType) =
            str.split(ELEMENT_SEPARATOR).let { array ->
                when (type) {
                    NBTType.INT_ARRAY -> NBTIntArray(array.map { it.toInt() }.toIntArray())
                    NBTType.BYTE_ARRAY -> NBTByteArray(array.map { it.toByte() }.toByteArray())
                    NBTType.LONG_ARRAY -> NBTLongArray(array.map { it.toLong() }.toLongArray())
                    else -> null
                }
            }

        private fun convertSpecialString(str: String, type: NBTType) =
            if (type == NBTType.COMPOUND || type == NBTType.LIST) deserializeFromJson(Json.parseToJsonElement(str)) else null

    }

}