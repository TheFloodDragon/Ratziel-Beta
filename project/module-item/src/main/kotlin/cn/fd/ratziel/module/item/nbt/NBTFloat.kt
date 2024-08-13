package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTFloat
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:23
 */
class NBTFloat private constructor(rawData: Any) : NBTData(rawData, NBTType.FLOAT) {

    constructor(value: Float) : this(new(value))

    override val content get() = NMSUtil.NtFloat.sourceField.get(data) as Float

    companion object {

        @JvmStatic
        fun new(value: Float) = NMSUtil.NtFloat.constructor.instance(value)!!

        @JvmStatic
        fun of(raw: Any) = if (NMSUtil.NtFloat.isOwnClass(raw::class.java)) NBTFloat(raw) else throw UnsupportedTypeException(raw)

    }

}