package cn.fd.ratziel.item.api.meta

import cn.fd.ratziel.item.api.meta.ItemCharacteristic
import cn.fd.ratziel.item.api.meta.ItemDisplay
import cn.fd.ratziel.item.api.meta.ItemDurability
import taboolib.module.nms.ItemTag

/**
 * ItemMetadata - 物品元数据
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:12
 */
interface ItemMetadata {

    /**
     * 物品显示部分
     */
    val display: ItemDisplay?

    /**
     * 物品特性
     */
    val characteristic: ItemCharacteristic?

    /**
     * 物品耐久
     */
    val durability: ItemDurability?

    /**
     * 自定义NBT
     */
    val nbt : ItemTag?

}