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
        else throw UnsupportedTypeException(encoder)
    }

    override fun deserialize(decoder: Decoder): NbtTag {
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
            is NbtList<*> -> serializeToJsonArray(target)
            // 基础类型序列化
            else -> JsonPrimitive(serializeToString(target))
        }

        /**
         * 序列化精确类型
         */
        fun serializeToString(target: NbtTag): String = when (target) {
            // Basic
            is NbtByte -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.BYTE.simpleName
            is NbtShort -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.SHORT.simpleName
            is NbtInt -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.INT.simpleName
            is NbtLong -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.LONG.simpleName
            is NbtFloat -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.FLOAT.simpleName
            is NbtDouble -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.DOUBLE.simpleName
            is NbtString -> target.content + EXACT_TYPE_CHAR + NBTType.STRING.simpleName
            // Array
            is NbtIntArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NBTType.INT_ARRAY.simpleName
            is NbtByteArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NBTType.BYTE_ARRAY.simpleName
            is NbtLongArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NBTType.LONG_ARRAY.simpleName
            // Special Type
            is NbtCompound -> serializeToJsonObject(target).toString() + EXACT_TYPE_CHAR + NBTType.COMPOUND.simpleName
            is NbtList<*> -> serializeToJsonArray(target).toString() + EXACT_TYPE_CHAR + NBTType.LIST.simpleName
            else -> throw UnsupportedTypeException(target.type)
        }

        fun serializeToJsonObject(target: NbtCompound) = buildJsonObject { target.forEach { put(it.key, serializeToJson(it.value)) } }

        fun serializeToJsonArray(target: NbtList<*>) = buildJsonArray { target.forEach { add(serializeToJson(it)) } }

        /**
         * 反序列化精确类型
         */
        fun deserializeFromString(target: String): NbtTag {
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
            NBTType.BYTE -> NbtByte(str.toByte())
            NBTType.DOUBLE -> NbtDouble(str.toDouble())
            NBTType.SHORT -> NbtShort(str.toShort())
            NBTType.LONG -> NbtLong(str.toLong())
            NBTType.FLOAT -> NbtFloat(str.toFloat())
            NBTType.INT -> NbtInt(str.toInt())
            NBTType.STRING -> NbtString(str)
            else -> null
        }

        private fun convertArrayString(str: String, type: NBTType) =
            str.split(ELEMENT_SEPARATOR).let { array ->
                when (type) {
                    NBTType.INT_ARRAY -> NbtIntArray(array.map { it.toInt() }.toIntArray())
                    NBTType.BYTE_ARRAY -> NbtByteArray(array.map { it.toByte() }.toByteArray())
                    NBTType.LONG_ARRAY -> NbtLongArray(array.map { it.toLong() }.toLongArray())
                    else -> null
                }
            }

        private fun convertSpecialString(str: String, type: NBTType) =
            if (type == NBTType.COMPOUND || type == NBTType.LIST) deserializeFromJson(Json.parseToJsonElement(str)) else null

    }

}