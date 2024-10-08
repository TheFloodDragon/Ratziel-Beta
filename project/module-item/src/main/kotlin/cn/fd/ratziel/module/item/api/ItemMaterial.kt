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
     * 材料ID (低版本)
     */
    val id: Int

    /**
     * 材料的默认最大堆叠数量
     */
    val maxStackSize: Int

    /**
     * 材料的默认最大耐久度
     */
    val maxDurability: Int

    /**
     * 判断材料是否为空
     */
    fun isEmpty() = this == EMPTY

    companion object {

        /**
         * 空材料
         */
        @JvmField
        val EMPTY = object : ItemMaterial {
            override val name = "AIR"
            override val maxStackSize = 0
            override val maxDurability = 0
            override val id = -10086
        }

    }

}