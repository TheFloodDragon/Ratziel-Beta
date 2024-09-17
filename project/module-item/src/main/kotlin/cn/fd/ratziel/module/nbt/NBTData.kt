package cn.fd.ratziel.module.nbt

/**
 * NBTData - NBT数据
 *
 * @author TheFloodDragon
 * @since 2024/3/15 19:18
 */
interface NBTData {

    /**
     * 数据类型
     */
    val type: NBTType

    /**
     * 数据内容
     */
    val content: Any

    /**
     * 克隆数据
     * @return 克隆出的新数据
     */
    fun clone(): NBTData

    override fun equals(other: Any?): Boolean

    override fun toString(): String

    override fun hashCode(): Int

}