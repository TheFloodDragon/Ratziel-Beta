package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTLongArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:23
 */
class NBTLongArray private constructor(rawData: Any) : NBTData(rawData, NBTType.LONG_ARRAY) {

    constructor(value: LongArray) : this(new(value))

    override val content get() = NMSUtil.NtLongArray.sourceField.get(data) as LongArray

    companion object {

        @JvmStatic
        fun new(value: LongArray) = NMSUtil.NtLongArray.constructor.instance(value)!!

        @JvmStatic
        fun of(raw: Any) = if (NMSUtil.NtLongArray.isOwnClass(raw::class.java)) NBTLongArray(raw) else throw UnsupportedTypeException(raw)

    }

}