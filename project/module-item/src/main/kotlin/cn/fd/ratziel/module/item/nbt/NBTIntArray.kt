package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTIntArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:22
 */
class NBTIntArray(rawData: Any) : NBTData(rawData, NBTType.INT_ARRAY) {

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    val content get() = NMSUtil.NtIntArray.sourceField.get(data) as IntArray

    companion object {

        fun new(value: IntArray) = NMSUtil.NtIntArray.constructor.instance(value)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtIntArray.nmsClass.isAssignableFrom(clazz::class.java)

    }

}