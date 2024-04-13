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
     * 物品的当前耐久
     */
    var currentDurability: Int?

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

    /**
     * 通过[ItemMaterial]获取物品最大耐久值
     */
    fun getMaxDurability(material: ItemMaterial): Int

    /**
     * 获取物品损伤值
     */
    fun getDamage(): Int?

    /**
     * 通过[ItemMaterial]获取物品损伤值
     */
    fun getDamage(material: ItemMaterial): Int

    /**
     * 设置物品损伤值
     */
    fun setDamage(value: Int)

    /**
     * 通过[ItemMaterial]设置物品损伤值
     */
    fun setDamage(value: Int,material: ItemMaterial)

}