package cn.fd.ratziel.module.nbt

/**
 * NBTData - NBT数据
 *
 * @author TheFloodDragon
 * @since 2024/3/15 19:18
 */
abstract class NBTData(
    /**
     * 数据类型
     */
    @JvmField val type: NBTType
) {

    /**
     * 数据内容
     */
    abstract val content: Any

    /**
     * 克隆数据
     * @return 克隆出的新数据
     */
    abstract fun clone(): NBTData

    override fun equals(other: Any?) = content == other

    override fun toString() = content.toString()

    override fun hashCode() = content.hashCode()

}