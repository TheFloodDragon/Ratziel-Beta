package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTString
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:13
 */
class NBTString private constructor(rawData: Any) : NBTData(rawData, NBTType.STRING) {

    constructor(value: String) : this(new(value))

    override val content get() = NMSUtil.NtString.sourceField.get(data) as String

    companion object {

        @JvmStatic
        fun new(value: String) = NMSUtil.NtString.constructor.instance(value)!!

        @JvmStatic
        fun of(raw: Any) = if (NMSUtil.NtString.isOwnClass(raw::class.java)) NBTString(raw) else throw UnsupportedTypeException(raw)

    }

}