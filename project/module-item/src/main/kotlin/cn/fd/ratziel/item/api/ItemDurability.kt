package cn.fd.ratziel.item.api

/**
 * ItemDurability - 物品耐久
 *
 * 有最大耐久和当前耐久
 * 不采用原版的耐久机制
 *
 * ps: 有耐久的都可以修复的吧
 *
 * @author TheFloodDragon
 * @since 2023/10/15 8:42
 */
interface ItemDurability {

    /**
     * 最大耐久
     */
    val maxDurability: Int?

    /**
     * 当前耐久
     */
    val currentDurability: Int?

    /**
     * 修复消耗
     */
    val repairCost: Int?

}