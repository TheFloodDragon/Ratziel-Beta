@file:Suppress("KDocUnresolvedReference", "LeakingThis")

package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import taboolib.module.nms.*


/**
 * NBT别名
 */
typealias NBTDataType = ItemTagType
typealias TiNBTData = ItemTagData
typealias TiNBTTag = ItemTag
typealias TiNBTList = ItemTagList

/**
 * NBTData - NBT 数据
 *
 * @author TheFloodDragon
 * @since 2023/11/24 21:32
 */
abstract class NBTData(
    /**
     * NBT数据 - 有两种形式
     *   1. [taboolib.ItemTag] 的 [TiNBT]
     *   2. [net.minecraft.nbt] 的 [NmsNBT]
     */
    protected open var data: Any,
    val type: NBTDataType,
) {

    /**
     * 类型检查 - 阻止不规范的类型
     */
    init {
        if (!isTiNBT() && !isNmsNBT()) throw UnsupportedTypeException(data)
    }

    /**
     * NBT数据类型是否为 [taboolib.ItemTag] 的 [TiNBT]
     */
    open fun isTiNBT() = data is TiNBTData

    /**
     * NBT数据类型是否为 [net.minecraft.nbt] 的 [NmsNBT]
     */
    open fun isNmsNBT() = checkIsNmsNBT(data)

    /**
     * 获取 [TiNBT] 形式
     */
    open fun getAsTiNBT() = if (isNmsNBT()) toTiNBT(data) else data as TiNBTData

    /**
     * 获取 [NmsNBT] 形式
     */
    open fun getAsNmsNBT() = if (isTiNBT()) toNmsNBT(data as TiNBTData) else data

    /**
     * 通过不安全的方式获取数据
     */
    open fun unsafeData() = data

    override fun equals(other: Any?) =
        // 如果为同类型,则直接调用各自的方法
        if ((data is TiNBTData && other is TiNBTData) || (checkIsNmsNBT(data) && checkIsNmsNBT(other)))
            data == other
        else { // 如果不同,则转换成 TiNBT 再判断
            if (data is TiNBTData) data == other?.let { toTiNBT(it) }
            else other == toTiNBT(data)
        }

    override fun toString() = data.toString()

    override fun hashCode() = data.hashCode()

}