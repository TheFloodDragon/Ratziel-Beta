package cn.fd.ratziel.module.item.nbt

/**
 * NBTLongArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:23
 */
class NBTLongArray(rawData: Any) : NBTData(rawData, NBTType.LONG_ARRAY) {

    constructor(value: LongArray) : this(new(value))

    override val content get() = NMSUtil.NtLongArray.sourceField.get(data) as LongArray

    companion object {

        @JvmStatic
        fun new(value: LongArray) = NMSUtil.NtLongArray.constructor.instance(value)!!

    }

}