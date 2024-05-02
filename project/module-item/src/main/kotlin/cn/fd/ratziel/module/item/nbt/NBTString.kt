package cn.fd.ratziel.module.item.nbt

/**
 * NBTString
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:13
 */
class NBTString(rawData: Any) : NBTData(rawData, NBTType.STRING) {

    constructor(value: String) : this(new(value))

    override val content get() = NMSUtil.NtString.sourceField.get(data) as String

    companion object {

        @JvmStatic
        fun new(value: String) = NMSUtil.NtString.constructor.instance(value)!!

    }

}