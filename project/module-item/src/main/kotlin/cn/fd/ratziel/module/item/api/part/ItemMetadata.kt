package cn.fd.ratziel.module.item.api.part

import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.api.ItemMaterial

/**
 * ItemMetadata
 *
 * @author TheFloodDragon
 * @since 2024/4/4 20:06
 */
interface ItemMetadata : ItemComponent {

    /**
     * 物品材料
     */
    var material: ItemMaterial

    /**
     * 物品显示部分
     */
    var display: ItemDisplay

    /**
     * 物品耐久部分
     */
    var durability: ItemDurability

    /**
     * 物品杂项部分
     */
    var sundry: ItemSundry

}