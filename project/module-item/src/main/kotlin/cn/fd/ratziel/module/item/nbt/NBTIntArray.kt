package cn.fd.ratziel.module.item.nbt

/**
 * NBTIntArray
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:22
 */
class NBTIntArray(rawData: Any) : NBTData(rawData, NBTType.INT_ARRAY) {

    constructor(value: IntArray) : this(new(value))

    override val content get() = NMSUtil.NtIntArray.sourceField.get(data) as IntArray

    companion object {

        @JvmStatic
        fun new(value: IntArray) = NMSUtil.NtIntArray.constructor.instance(value)!!

    }

}