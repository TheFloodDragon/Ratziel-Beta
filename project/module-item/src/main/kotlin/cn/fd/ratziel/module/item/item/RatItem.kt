package cn.fd.ratziel.module.item.item

import cn.fd.ratziel.module.item.api.ItemInfo
import cn.fd.ratziel.module.item.api.RatzielItem
import cn.fd.ratziel.module.item.item.meta.VItemMeta

/**
 * RatItem - Ratziel 物品
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:10
 */
data class RatItem(
    /**
     * 物品信息
     */
    override val info: ItemInfo,
    /**
     * 物品元数据
     */
    override var meta: VItemMeta,
) : RatzielItem