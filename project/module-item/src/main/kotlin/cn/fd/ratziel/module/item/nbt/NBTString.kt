package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTString
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:13
 */
class NBTString(rawData: Any) : NBTData(rawData, NBTType.STRING) {

    init {
        if (!isOwnNmsClass(rawData::class.java)) throw UnsupportedTypeException(rawData)
    }

    val content get() = NMSUtil.NtString.sourceField.get(data) as String

    companion object {

        fun new(value: String) = NMSUtil.NtString.constructor.instance(value)!!

        fun isOwnNmsClass(clazz: Class<*>) = NMSUtil.NtString.nmsClass.isAssignableFrom(clazz::class.java)

    }

}