package cn.fd.ratziel.module.item.nbt

/**
 * NBTByte
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:20
 */
class NBTByte(rawData: Any) : NBTData(rawData, NBTType.BYTE) {

    constructor(value: Boolean) : this(new(value))

    constructor(value: Byte) : this(new(value))

    override val content get() = NMSUtil.NtByte.sourceField.get(data) as Byte

    companion object {

        const val BYTE_FALSE: Byte = 0
        const val BYTE_TRUE: Byte = 1

        @JvmStatic
        fun new(value: Boolean) = new(adapt(value))

        @JvmStatic
        fun new(value: Byte) = NMSUtil.NtByte.constructor.instance(value)!!

        @JvmStatic
        fun adapt(from: Byte): Boolean = adaptOrNull(from) ?: false

        @JvmStatic
        fun adaptOrNull(from: Byte): Boolean? = if (from == BYTE_TRUE) true else if (from == BYTE_FALSE) false else null

        @JvmStatic
        fun adapt(from: Boolean): Byte = if (from) BYTE_TRUE else BYTE_FALSE

    }

}