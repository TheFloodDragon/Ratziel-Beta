package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTShort
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:24
 */
class NBTShort(rawData: Any) : NBTData(rawData, NBTType.SHORT) {

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    val content get() = NMSUtil.NtShort.sourceField.get(data) as Short

    companion object {

        fun new(value: Short) = NMSUtil.NtShort.constructor.instance(value)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtShort.isNmsClass(clazz)

    }

}