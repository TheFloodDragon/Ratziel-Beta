package cn.fd.ratziel.module.itemengine.api

import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.api.part.ItemInfo
import cn.fd.ratziel.module.itemengine.api.part.ItemPart

/**
 * NeoItem - 物品
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:21
 */
interface NeoItem : ItemPart {
    /**
     * 物品信息
     */
    val info: ItemInfo

    /**
     * 物品属性列表
     */
    val attributes: List<ItemAttribute<*>>
}