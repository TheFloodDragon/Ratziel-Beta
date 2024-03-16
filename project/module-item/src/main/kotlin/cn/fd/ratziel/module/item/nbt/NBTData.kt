package cn.fd.ratziel.module.item.nbt

/**
 * NBTData - NBT数据
 *
 * @author TheFloodDragon
 * @since 2024/3/15 19:18
 */
abstract class NBTData(
    /**
     * NBT原始数据 (NMS)
     */
    @JvmField protected var data: Any,
    /**
     * NBT数据类型
     */
    @JvmField val type: NBTType
) {

    /**
     * 获取原始数据
     */
    open fun getData() = data

    override fun equals(other: Any?) = (if (other is NBTData) other.getData() else other) == data

    override fun toString() = data.toString()

    override fun hashCode() = data.hashCode()

}