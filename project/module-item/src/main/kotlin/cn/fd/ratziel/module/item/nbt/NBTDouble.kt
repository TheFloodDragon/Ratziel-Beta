package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTDouble
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:18
 */
class NBTDouble(rawData: Any) : NBTData(rawData, NBTType.DOUBLE) {

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    val content get() = NMSUtil.NtDouble.sourceField.get(data) as Double

    companion object {

        fun new(value: Double) = NMSUtil.NtDouble.constructor.instance(value)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtDouble.isNmsClass(clazz)

    }

}