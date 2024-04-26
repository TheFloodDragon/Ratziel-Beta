package cn.fd.ratziel.module.item.nbt

/**
 * NBTShort
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:24
 */
class NBTShort(rawData: Any) : NBTData(rawData, NBTType.SHORT) {

    constructor(value: Short) : this(new(value))

    val content get() = NMSUtil.NtShort.sourceField.get(data) as Short

    companion object {

        @JvmStatic
        fun new(value: Short) = NMSUtil.NtShort.constructor.instance(value)!!

    }

}