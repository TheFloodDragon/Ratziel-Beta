package cn.fd.ratziel.module.item.nbt

/**
 * NBTFloat
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:23
 */
class NBTFloat(rawData: Any) : NBTData(rawData, NBTType.FLOAT) {

    constructor(value: Float) : this(new(value))

    val content get() = NMSUtil.NtFloat.sourceField.get(data) as Float

    companion object {

        @JvmStatic
        fun new(value: Float) = NMSUtil.NtFloat.constructor.instance(value)!!

    }

}