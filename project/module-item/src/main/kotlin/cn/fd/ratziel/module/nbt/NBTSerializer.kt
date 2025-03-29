package cn.fd.ratziel.module.nbt

import cn.altawk.nbt.tag.*
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
object NBTSerializer : KSerializer<NbtTag> {

    override val descriptor = PrimitiveSerialDescriptor("nbt.NbtTag", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: NbtTag) {
        if (encoder is JsonEncoder)
            encoder.encodeJsonElement(Converter.serializeToJson(value))
        else super.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): NbtTag {
        return if (decoder is JsonDecoder)
            Converter.deserializeFromJson(decoder.decodeJsonElement())
        else super.deserialize(decoder)
    }

    object Converter {

        /**
         * 精确类型控制符
         */
        const val EXACT_TYPE_CHAR = ";"

        const val ELEMENT_SEPARATOR = ","

        /**
         * 将 [JsonElement] 反序列化成 [NbtTag]
         */
        fun deserializeFromJson(json: JsonElement, source: NbtCompound = NbtCompound()): NbtTag =
            when (json) {
                is JsonPrimitive -> deserializeFromString(json.content)
                is JsonArray -> NbtList.of(json.map { deserializeFromJson(it, NbtCompound()) })
                is JsonObject -> source.also { tag ->
                    json.forEach {
                        val newSource = tag.getDeep(it.key) as? NbtCompound ?: NbtCompound()
                        tag.putDeep(it.key, deserializeFromJson(it.value, newSource))
                    }
                }
            }

        /**
         * 将 [NbtTag] 序列化成 [JsonElement]
         */
        fun serializeToJson(target: NbtTag): JsonElement = when (target) {
            // 特殊类型序列化
            is NbtCompound -> serializeToJsonObject(target)
            is NbtList -> serializeToJsonArray(target)
            // 基础类型序列化
            else -> JsonPrimitive(serializeToString(target))
        }

        /**
         * 序列化精确类型
         */
        fun serializeToString(target: NbtTag): String = when (target) {
            // Basic
            is NbtByte -> target.content.toString() + EXACT_TYPE_CHAR + NbtType.BYTE.name
            is NbtShort -> target.content.toString() + EXACT_TYPE_CHAR + NbtType.SHORT.name
            is NbtInt -> target.content.toString() + EXACT_TYPE_CHAR + NbtType.INT.name
            is NbtLong -> target.content.toString() + EXACT_TYPE_CHAR + NbtType.LONG.name
            is NbtFloat -> target.content.toString() + EXACT_TYPE_CHAR + NbtType.FLOAT.name
            is NbtDouble -> target.content.toString() + EXACT_TYPE_CHAR + NbtType.DOUBLE.name
            is NbtString -> target.content + EXACT_TYPE_CHAR + NbtType.STRING.name
            // Array
            is NbtIntArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NbtType.INT_ARRAY.name
            is NbtByteArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NbtType.BYTE_ARRAY.name
            is NbtLongArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NbtType.LONG_ARRAY.name
            // Special Type
            is NbtCompound -> serializeToJsonObject(target).toString() + EXACT_TYPE_CHAR + NbtType.COMPOUND.name
            is NbtList -> serializeToJsonArray(target).toString() + EXACT_TYPE_CHAR + NbtType.LIST.name
            else -> throw UnsupportedTypeException(target.type)
        }

        fun serializeToJsonObject(target: NbtCompound) = buildJsonObject { target.forEach { put(it.key, serializeToJson(it.value)) } }

        fun serializeToJsonArray(target: NbtList) = buildJsonArray { target.forEach { add(serializeToJson(it)) } }

        /**
         * 反序列化精确类型
         */
        fun deserializeFromString(target: String): NbtTag {
            if (!target.contains(EXACT_TYPE_CHAR)) return adaptString(target)

            val typeStr = target.substringAfterLast(EXACT_TYPE_CHAR)
            val dataStr = target.substringBeforeLast(EXACT_TYPE_CHAR)

            // 匹配类型
            val type = NbtType.entries.find { it.name == typeStr } ?: return adaptString(target)

            return convertBasicString(dataStr, type)
                ?: convertArrayString(dataStr, type)
                ?: convertSpecialString(dataStr, type)
                ?: throw UnsupportedTypeException(type)
        }

        private fun adaptString(str: String) = NbtAdapter.box(str.adapt())

        private fun convertBasicString(str: String, type: NbtType) = when (type) {
            NbtType.BYTE -> NbtByte(str.toByte())
            NbtType.DOUBLE -> NbtDouble(str.toDouble())
            NbtType.SHORT -> NbtShort(str.toShort())
            NbtType.LONG -> NbtLong(str.toLong())
            NbtType.FLOAT -> NbtFloat(str.toFloat())
            NbtType.INT -> NbtInt(str.toInt())
            NbtType.STRING -> NbtString(str)
            else -> null
        }

        private fun convertArrayString(str: String, type: NbtType) =
            str.split(ELEMENT_SEPARATOR).let { array ->
                when (type) {
                    NbtType.INT_ARRAY -> NbtIntArray(array.map { it.toInt() }.toIntArray())
                    NbtType.BYTE_ARRAY -> NbtByteArray(array.map { it.toByte() }.toByteArray())
                    NbtType.LONG_ARRAY -> NbtLongArray(array.map { it.toLong() }.toLongArray())
                    else -> null
                }
            }

        private fun convertSpecialString(str: String, type: NbtType) =
            if (type == NbtType.COMPOUND || type == NbtType.LIST) deserializeFromJson(Json.parseToJsonElement(str)) else null

    }

}