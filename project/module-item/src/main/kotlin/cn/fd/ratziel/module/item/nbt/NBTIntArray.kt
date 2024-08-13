package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTIntArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:22
 */
class NBTIntArray private constructor(rawData: Any) : NBTData(rawData, NBTType.INT_ARRAY) {

    constructor(value: IntArray) : this(new(value))

    override val content get() = NMSUtil.NtIntArray.sourceField.get(data) as IntArray

    companion object {

        @JvmStatic
        fun new(value: IntArray) = NMSUtil.NtIntArray.constructor.instance(value)!!

        @JvmStatic
        fun of(raw: Any) = if (NMSUtil.NtIntArray.isOwnClass(raw::class.java)) NBTIntArray(raw) else throw UnsupportedTypeException(raw)

    }

}