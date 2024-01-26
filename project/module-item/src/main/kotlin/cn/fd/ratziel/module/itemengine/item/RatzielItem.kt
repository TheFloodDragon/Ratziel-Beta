package cn.fd.ratziel.module.itemengine.item

import cn.fd.ratziel.module.itemengine.api.NeoItem
import cn.fd.ratziel.module.itemengine.api.attribute.ItemAttribute
import cn.fd.ratziel.module.itemengine.api.part.ItemInfo

/**
 * RatzielItem - Ratziel 物品
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:10
 */
data class RatzielItem(
    /**
     * 物品信息
     */
    override val info: ItemInfo,
    /**
     * 物品属性列表
     */
    override val attributes: List<ItemAttribute<*>>,
) : NeoItem