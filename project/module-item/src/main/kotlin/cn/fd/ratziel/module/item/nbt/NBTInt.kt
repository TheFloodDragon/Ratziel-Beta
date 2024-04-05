package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTInt
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:14
 */
class NBTInt(rawData: Any) : NBTData(rawData, NBTType.INT) {

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    val content get() = NMSUtil.NtInt.sourceField.get(data) as Int

    companion object {

        fun new(value: Int) = NMSUtil.NtInt.constructor.instance(value)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtInt.isNmsClass(clazz)

    }

}