package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTLong
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:22
 */
class NBTLong(rawData: Any) : NBTData(rawData, NBTType.LONG) {

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    val content get() = NMSUtil.NtLong.sourceField.get(data) as Long

    companion object {

        fun new(value: Long) = NMSUtil.NtLong.constructor.instance(value)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtLong.isNmsClass(clazz)

    }

}