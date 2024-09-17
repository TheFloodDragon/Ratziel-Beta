package cn.fd.ratziel.module.nbt

/**
 * NBTByte
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:20
 */
@JvmInline
value class NBTByte(override val content: Byte) : NBTData {

    constructor(value: Boolean) : this(toByte(value))

    override fun clone() = NBTByte(content)

    override fun toString() = content.toString() + "b"

    override val type get() = NBTType.BYTE

    companion object {

        const val BYTE_FALSE: Byte = 0
        const val BYTE_TRUE: Byte = 1

        @JvmStatic
        fun parseBooleanOrFalse(from: Byte): Boolean = parseBoolean(from) ?: false

        @JvmStatic
        fun parseBoolean(from: Byte): Boolean? = if (from == BYTE_TRUE) true else if (from == BYTE_FALSE) false else null

        @JvmStatic
        fun toByte(from: Boolean): Byte = if (from) BYTE_TRUE else BYTE_FALSE

    }

}