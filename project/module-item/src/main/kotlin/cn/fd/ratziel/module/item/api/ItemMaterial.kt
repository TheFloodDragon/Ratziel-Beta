package cn.fd.ratziel.module.item.api

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

    companion object {

        private const val EMPTY_ID = -10086

        /**
         * 空材料
         */
        val EMPTY = object : ItemMaterial {
            override val name = "AIR"
            override val maxStackSize = 0
            override val maxDurability = 0
            override val id = EMPTY_ID
        }

        /**
         * 判断材料是否为空
         */
        fun isEmpty(material: ItemMaterial) = material.id == EMPTY_ID

    }

}