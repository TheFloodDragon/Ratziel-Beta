package cn.fd.ratziel.module.item.api

import cn.fd.ratziel.module.item.nbt.NBTCompound

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
    val material: ItemMaterial

    /**
     * 物品NBT
     */
    val nbt: NBTCompound

    /**
     * 物品数量
     */
    val amount: Int

}