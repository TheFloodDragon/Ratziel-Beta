package cn.fd.ratziel.item.api.meta

import net.kyori.adventure.text.Component

/**
 * ItemDisplay - 物品的显示部分
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:08
 */
interface ItemDisplay {

    /**
     * 物品名称
     */
    val name: Component?

    /**
     * 物品本地化名称
     */
    val localizedName: Component?

    /**
     * 物品描述
     */
    val lore: List<Component>

}