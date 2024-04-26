package cn.fd.ratziel.module.item.nbt

/**
 * NBTDouble
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:18
 */
class NBTDouble(rawData: Any) : NBTData(rawData, NBTType.DOUBLE) {

    constructor(value: Double) : this(new(value))

    val content get() = NMSUtil.NtDouble.sourceField.get(data) as Double

    companion object {

        @JvmStatic
        fun new(value: Double) = NMSUtil.NtDouble.constructor.instance(value)!!

    }

}