package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTShort
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:24
 */
class NBTShort private constructor(rawData: Any) : NBTData(rawData, NBTType.SHORT) {

    constructor(value: Short) : this(new(value))

    override val content get() = NMSUtil.NtShort.sourceField.get(data) as Short

    companion object {

        @JvmStatic
        fun new(value: Short) = NMSUtil.NtShort.constructor.instance(value)!!

        @JvmStatic
        fun of(raw: Any) = if (NMSUtil.NtShort.isOwnClass(raw::class.java)) NBTShort(raw) else throw UnsupportedTypeException(raw)

    }

}