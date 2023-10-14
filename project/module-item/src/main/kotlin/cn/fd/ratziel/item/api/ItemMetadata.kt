package cn.fd.ratziel.item.api

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

}