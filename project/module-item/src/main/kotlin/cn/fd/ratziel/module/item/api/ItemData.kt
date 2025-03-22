package cn.fd.ratziel.module.item.api

import cn.altawk.nbt.tag.NbtCompound

/**
 * ItemData - 物品数据
 *
 * @author TheFloodDragon
 * @since 2024/4/27 09:35
 */
interface ItemData {

    /**
     * 物品材料
     */
    var material: ItemMaterial

    /**
     * 物品标签数据
     */
    var tag: NbtCompound

    /**
     * 物品数量
     */
    var amount: Int

    /**
     * 合并数据
     */
    fun merge(other: ItemData, replace: Boolean = true)

    /**
     * 克隆数据
     */
    fun clone(): ItemData

    object Empty : ItemData {
        override var material: ItemMaterial get() = ItemMaterial.EMPTY; set(_) = Unit
        override var tag: NbtCompound get() = NbtCompound(); set(_) = Unit
        override var amount: Int get() = 0; set(_) = Unit
        override fun merge(other: ItemData, replace: Boolean) = Unit
        override fun clone() = this
    }

}