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
     * 克隆数据
     */
    fun clone(): ItemData

}