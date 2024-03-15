package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTByteArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:21
 */
class NBTByteArray(rawData: Any) : NBTData(rawData, NBTType.BYTE_ARRAY) {

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    companion object {

        fun new(value: ByteArray) = NMSUtil.NtByteArray.constructor.instance(value)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtByteArray.nmsClass.isAssignableFrom(clazz::class.java)

    }

}