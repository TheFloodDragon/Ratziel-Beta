package cn.fd.ratziel.module.itemengine.api.meta

import cn.fd.ratziel.module.itemengine.api.ItemPart
import cn.fd.ratziel.module.itemengine.nbt.NBTTag

/**
 * ItemMetadata - 物品元数据
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:12
 */
interface ItemMetadata : ItemPart {

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
    val nbt: NBTTag?

}