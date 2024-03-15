package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTLongArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:23
 */
class NBTLongArray(rawData: Any) : NBTData(rawData, NBTType.LONG_ARRAY) {

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    val content get() = NMSUtil.NtLongArray.sourceField.get(data) as LongArray

    companion object {

        fun new(value: LongArray) = NMSUtil.NtLongArray.constructor.instance(value)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtLongArray.nmsClass.isAssignableFrom(clazz::class.java)

    }

}