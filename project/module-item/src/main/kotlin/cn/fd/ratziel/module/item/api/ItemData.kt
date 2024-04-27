package cn.fd.ratziel.module.item.api

import cn.fd.ratziel.module.item.nbt.NBTCompound

/**
 * ItemData
 * 
 * @author TheFloodDragon
 * @since 2024/4/27 09:35
*/
data class ItemData(
    /**
     * 物品材料
     */
    var material: ItemMaterial=ItemMaterial.EMPTY,
    /**
     * 物品NBT
     */
    var nbt: NBTCompound = NBTCompound(),
)