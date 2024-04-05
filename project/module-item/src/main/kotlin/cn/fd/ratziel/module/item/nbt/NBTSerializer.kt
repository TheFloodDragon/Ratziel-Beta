package cn.fd.ratziel.module.item.nbt

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

    override fun deserialize(decoder: Decoder): NBTData = Mapper.deserializeFromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: NBTData) = encoder.encodeString(Mapper.serializeToString(value))

    object Mapper {

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
                is JsonArray -> NBTConverter.BasicConverter.convertList(json.map { deserializeFromJson(it, NBTCompound()) })
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
        fun serializeToJson(target: NBTData): JsonElement =
            // 特殊类型序列化
            when (target) {
                is NBTCompound -> serializeToJsonObject(target)
                is NBTList -> serializeToJsonArray(target)
                else -> null
            } ?: JsonPrimitive(serializeToString(target)) // 基础类型序列化

        /**
         * 序列化精确类型
         */
        fun serializeToString(target: NBTData): String = when (target) {
            // Basic
            is NBTByte -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.BYTE.signName
            is NBTShort -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.SHORT.signName
            is NBTInt -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.INT.signName
            is NBTLong -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.LONG.signName
            is NBTFloat -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.FLOAT.signName
            is NBTDouble -> target.content.toString() + EXACT_TYPE_CHAR + NBTType.DOUBLE.signName
            is NBTString -> target.content + EXACT_TYPE_CHAR + NBTType.STRING.signName
            // Array
            is NBTIntArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NBTType.INT_ARRAY.signName
            is NBTByteArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NBTType.BYTE_ARRAY.signName
            is NBTLongArray -> target.content.joinToString(ELEMENT_SEPARATOR) { it.toString() } + EXACT_TYPE_CHAR + NBTType.LONG_ARRAY.signName
            // Special Type
            is NBTCompound -> serializeToJsonObject(target).toString() + EXACT_TYPE_CHAR + NBTType.COMPOUND.signName
            is NBTList -> serializeToJsonArray(target).toString() + EXACT_TYPE_CHAR + NBTType.LIST.signName
            else -> throw UnsupportedTypeException(target.type)
        }

        fun serializeToJsonObject(target: NBTCompound) = buildJsonObject { target.content.forEach { put(it.key, serializeToJson(it.value)) } }

        fun serializeToJsonArray(target: NBTList) = buildJsonArray { target.content.forEach { add(serializeToJson(it)) } }

        /**
         * 反序列化精确类型
         */
        fun deserializeFromString(target: String): NBTData {
            val typeStr = target.substringAfterLast(EXACT_TYPE_CHAR)
            val dataStr = target.substringBeforeLast(EXACT_TYPE_CHAR)

            // 匹配类型
            val type = NBTType.entries.find {
                it.alias.contains(typeStr) || it.signName == typeStr
            }
            // 无法找到时返回自适应后的
            if (type == null) return adaptString(target)

            return convertBasicString(dataStr, type)
                ?: convertArrayString(dataStr, type)
                ?: convertSpecialString(dataStr, type)
                ?: throw UnsupportedTypeException(type)
        }

        private fun adaptString(str: String) = NBTConverter.BasicConverter.convert(str.adapt())!!

        private fun convertBasicString(str: String, type: NBTType) = when (type) {
            NBTType.BYTE -> NBTByte(NBTByte.new(str.toByte()))
            NBTType.DOUBLE -> NBTDouble(NBTDouble.new(str.toDouble()))
            NBTType.SHORT -> NBTShort(NBTShort.new(str.toShort()))
            NBTType.LONG -> NBTLong(NBTLong.new(str.toLong()))
            NBTType.FLOAT -> NBTFloat(NBTFloat.new(str.toFloat()))
            NBTType.INT -> NBTInt(NBTInt.new(str.toInt()))
            NBTType.STRING -> NBTString(NBTString.new(str))
            else -> null
        }

        private fun convertArrayString(str: String, type: NBTType) =
            str.split(ELEMENT_SEPARATOR).let { array ->
                when (type) {
                    NBTType.INT_ARRAY -> NBTIntArray(NBTIntArray.new(array.map { it.toInt() }.toIntArray()))
                    NBTType.BYTE_ARRAY -> NBTByteArray(NBTByteArray.new(array.map { it.toByte() }.toByteArray()))
                    NBTType.LONG_ARRAY -> NBTLongArray(NBTLongArray.new(array.map { it.toLong() }.toLongArray()))
                    else -> null
                }
            }

        private fun convertSpecialString(str: String, type: NBTType) =
            if (type == NBTType.COMPOUND || type == NBTType.LIST) deserializeFromJson(Json.parseToJsonElement(str)) else null

    }

}