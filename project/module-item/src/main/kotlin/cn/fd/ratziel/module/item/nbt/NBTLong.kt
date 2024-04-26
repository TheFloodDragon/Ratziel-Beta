package cn.fd.ratziel.module.item.nbt

/**
 * NBTLong
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:22
 */
class NBTLong(rawData: Any) : NBTData(rawData, NBTType.LONG) {

    constructor(value: Long) : this(new(value))

    val content get() = NMSUtil.NtLong.sourceField.get(data) as Long

    companion object {

        @JvmStatic
        fun new(value: Long) = NMSUtil.NtLong.constructor.instance(value)!!

    }

}