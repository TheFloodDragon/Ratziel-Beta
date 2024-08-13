package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

/**
 * NBTByteArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:21
 */
class NBTByteArray private constructor(rawData: Any) : NBTData(rawData, NBTType.BYTE_ARRAY) {

    constructor(value: ByteArray) : this(new(value))

    override val content get() = NMSUtil.NtByteArray.sourceField.get(data) as ByteArray

    companion object {

        @JvmStatic
        fun new(value: ByteArray) = NMSUtil.NtByteArray.constructor.instance(value)!!

        @JvmStatic
        fun of(raw: Any) = if (NMSUtil.NtByteArray.isOwnClass(raw::class.java)) NBTByteArray(raw) else throw UnsupportedTypeException(raw)

    }

}