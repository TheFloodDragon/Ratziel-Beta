package cn.fd.ratziel.module.item.api.part

import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.nbt.NBTCompound

/**
 * ItemDurability
 *
 * @author TheFloodDragon
 * @since 2024/4/13 16:02
 */
interface ItemDurability : ItemComponent<ItemDurability, NBTCompound> {

    /**
     * 物品的最大耐久
     */
    var maxDurability: Int?

    /**
     * 物品的修复消耗
     */
    var repairCost: Int?

    /**
     * 物品是否无法破坏
     */
    var unbreakable: Boolean?

}