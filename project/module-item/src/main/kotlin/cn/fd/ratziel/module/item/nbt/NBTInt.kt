package cn.fd.ratziel.module.item.nbt

/**
 * NBTInt
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:14
 */
class NBTInt(rawData: Any) : NBTData(rawData, NBTType.INT) {

    constructor(value: Int) : this(new(value))

    override val content get() = NMSUtil.NtInt.sourceField.get(data) as Int

    companion object {

        @JvmStatic
        fun new(value: Int) = NMSUtil.NtInt.constructor.instance(value)!!

    }

}