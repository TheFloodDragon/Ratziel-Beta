package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException

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
    val type: NBTType
) {

    init {
        // 数据检验
        if (!isOwnClass(data::class.java)) throw UnsupportedTypeException(data)
    }

    /**
     * 供外部使用的内容 (通常是不可变的)
     */
    abstract val content: Any

    /**
     * 获取原始数据
     */
    open fun getData() = data

    /**
     * 判断目标类是否为对应NBT数据类型的NMS类
     */
    fun isOwnClass(clazz: Class<*>) = NMSUtil.inferUtil(type).nmsClass.isAssignableFrom(clazz)

    override fun equals(other: Any?) = (if (other is NBTData) other.getData() else other) == data

    override fun toString() = data.toString()

    override fun hashCode() = data.hashCode()

}