package cn.fd.ratziel.module.item.nbt

/**
 * NBTByteArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:21
 */
class NBTByteArray(rawData: Any) : NBTData(rawData, NBTType.BYTE_ARRAY) {

    constructor(value: ByteArray) : this(new(value))

    override val content get() = NMSUtil.NtByteArray.sourceField.get(data) as ByteArray

    companion object {

        @JvmStatic
        fun new(value: ByteArray) = NMSUtil.NtByteArray.constructor.instance(value)!!

    }

}