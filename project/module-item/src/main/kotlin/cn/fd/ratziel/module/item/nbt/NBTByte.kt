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

    val content get() = NMSUtil.NtByte.sourceField.get(data) as Byte

    val contentBoolean get() = contentBooleanOrNull ?: false

    val contentBooleanOrNull get() = if (content == BYTE_TRUE) true else if (content == BYTE_FALSE) false else null

    companion object {

        const val BYTE_FALSE: Byte = 0
        const val BYTE_TRUE: Byte = 1

        @JvmStatic
        fun new(value: Boolean) = new(if (value) BYTE_TRUE else BYTE_FALSE)

        @JvmStatic
        fun new(value: Byte) = NMSUtil.NtByte.constructor.instance(value)!!

    }

}