package cn.fd.ratziel.module.itemengine.item

import cn.fd.ratziel.module.itemengine.api.part.ItemInfo
import cn.fd.ratziel.module.itemengine.api.NeoItem
import cn.fd.ratziel.module.itemengine.item.meta.VItemMeta

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
     * 物品元数据
     */
    override var meta: VItemMeta,
) : NeoItem