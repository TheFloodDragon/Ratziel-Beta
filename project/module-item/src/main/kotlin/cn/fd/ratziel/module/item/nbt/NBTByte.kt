package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTByte
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:20
 */
class NBTByte(rawData: Any) : NBTData(rawData, NBTType.BYTE) {

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    companion object {

        const val BYTE_FALSE: Byte = 0
        const val BYTE_TRUE: Byte = 1

        fun new(value: Boolean) = new(if (value) BYTE_TRUE else BYTE_FALSE)

        fun new(value: Byte) = NMSUtil.NtByte.constructor.instance(value)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtByte.nmsClass.isAssignableFrom(clazz::class.java)

    }

}