package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTLong
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:22
 */
class NBTLong private constructor(rawData: Any) : NBTData(rawData, NBTType.LONG) {

    constructor(value: Long) : this(new(value))

    override val content get() = NMSUtil.NtLong.sourceField.get(data) as Long

    companion object {

        @JvmStatic
        fun new(value: Long) = NMSUtil.NtLong.constructor.instance(value)!!

        @JvmStatic
        fun of(raw: Any) = if (NMSUtil.NtLong.isOwnClass(raw::class.java)) NBTLong(raw) else throw UnsupportedTypeException(raw)

    }

}