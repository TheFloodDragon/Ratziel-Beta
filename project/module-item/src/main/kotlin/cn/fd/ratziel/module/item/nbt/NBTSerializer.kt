package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.serialization.primitiveDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * NBTSerializer
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:40
 */
object NBTSerializer : KSerializer<NBTCompound> {

    override val descriptor = primitiveDescriptor(PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): NBTCompound {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: NBTCompound) {
        TODO("Not yet implemented")
    }

    object Mapper {

        /**
         * 精确类型控制符
         */
        const val EXACT_TYPE_CHAR = ";"

        const val QUOTATION = "\""

        const val ELEMENT_SEPARATOR = ","

        /**
         * IntArray Example:
         *   1,2,3,4;i
         */

        /**
         * Simple List Example:
         *   [1,2b,3,abc,awa];list
         */
        const val LIST_START = "["
        const val LIST_END = "]"

        /**
         * Simple Compound Example:
         *   {key1=123,"key2"="cm=\"",key3=11b};cpd
         */
        const val COMPOUND_START = "{"
        const val COMPOUND_SET = "="
        const val COMPOUND_END = "}"

        /**
         * 反序列化精确类型
         */
        fun deserializePrimitive(target: String): NBTData {
            val typeStr = target.substringAfterLast(EXACT_TYPE_CHAR).lowercase()
            val dataStr = target.substringBeforeLast(EXACT_TYPE_CHAR)
            // 匹配类型
            val type = NBTType.entries.find {
                it.alias.contains(typeStr) && it.name.lowercase() == typeStr
            }
            // 无法找到时默认返回 NBTString 类型
            if (type == null) return NBTString(NBTString.new(target))

            return convertBasicString(dataStr, type)
                ?: convertArrayString(dataStr, type)
                ?: convertListString(dataStr, type)
                ?: convertMapString(dataStr, type)
                ?: throw UnsupportedOperationException("Unsupported NBT Type: $type")
        }

        private fun convertBasicString(str: String, type: NBTType) = when (type) {
            NBTType.BYTE -> NBTByte(NBTByte.new(str.toByte()))
            NBTType.DOUBLE -> NBTDouble(NBTDouble.new(str.toDouble()))
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

        private fun convertListString(str: String, type: NBTType): NBTList? =
            str.takeIf { type == NBTType.LIST }
                ?.let { s -> parseList(s).map { deserializePrimitive(it) } }
                ?.let { NBTConverter.BasicConverter.convertList(it) }

        private fun convertMapString(str: String, type: NBTType): NBTCompound? =
            TODO("不会")

        private fun parseList(source: String) = buildList {
            val buffer = StringBuilder()
            var isInQuotes = false
            val str = source.substring(source.indexOf(LIST_START), source.lastIndexOf(LIST_END))
            var index = 0
            while (index < str.length) {
                if (str.startsWith(QUOTATION, index)) {
                    index += QUOTATION.length  // 跳过这个标记
                    isInQuotes = !isInQuotes  // 切换引号内外状态
                    if (!isInQuotes) {
                        this.add(buffer.toString())
                        buffer.clear()
                    }
                } else {
                    if (isInQuotes || !str.startsWith(ELEMENT_SEPARATOR, index)) {
                        buffer.append(str[index])
                        index++  // 移动到下一个字符
                    } else {
                        index += ELEMENT_SEPARATOR.length  // 跳过逗号标记
                        if (buffer.isNotEmpty()) {
                            this.add(buffer.toString())
                            buffer.clear()
                        }
                    }
                }
            }
            // 添加最后一个元素
            if (buffer.isNotEmpty()) this.add(buffer.toString())
        }

        fun parseMap(source: String): Map<String, String> {
            TODO("不会")
        }


    }

}