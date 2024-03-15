package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTFloat
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:23
 */
class NBTFloat(rawData: Any) : NBTData(rawData, NBTType.FLOAT) {

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    val content get() = NMSUtil.NtFloat.sourceField.get(data) as Float

    companion object {

        fun new(value: Float) = NMSUtil.NtFloat.constructor.instance(value)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtFloat.nmsClass.isAssignableFrom(clazz::class.java)

    }

}