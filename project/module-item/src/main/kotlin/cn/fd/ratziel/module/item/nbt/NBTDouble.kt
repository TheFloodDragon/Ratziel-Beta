package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTDouble
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:18
 */
class NBTDouble private constructor(rawData: Any) : NBTData(rawData, NBTType.DOUBLE) {

    constructor(value: Double) : this(new(value))

    override val content get() = NMSUtil.NtDouble.sourceField.get(data) as Double

    companion object {

        @JvmStatic
        fun new(value: Double) = NMSUtil.NtDouble.constructor.instance(value)!!

        @JvmStatic
        fun of(raw: Any) = if (NMSUtil.NtDouble.isOwnClass(raw::class.java)) NBTDouble(raw) else throw UnsupportedTypeException(raw)

    }

}