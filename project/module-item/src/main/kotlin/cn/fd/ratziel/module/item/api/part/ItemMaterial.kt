package cn.fd.ratziel.module.item.api.part

/**
 * ItemMaterial - 物品材料
 *
 * @author TheFloodDragon
 * @since 2024/4/5 13:23
 */
interface ItemMaterial {

    /**
     * 材料名称
     */
    val name: String

    /**
     * 材料的默认最大堆叠数量
     */
    val maxStackSize: Int

    /**
     * 材料的默认最大耐久度
     */
    val maxDurability: Int

    /**
     * 材料标识符 (低版本)
     */
    val id: Int

}