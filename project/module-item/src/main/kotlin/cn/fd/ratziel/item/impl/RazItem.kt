package cn.fd.ratziel.item.impl

import cn.fd.ratziel.item.api.ItemInfo
import cn.fd.ratziel.item.api.RatzielItem
import cn.fd.ratziel.item.meta.VItemMeta

/**
 * RazItem - Ratziel 物品
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:10
 */
data class RazItem(
    /**
     * 物品信息
     */
    override val info: ItemInfo,
    /**
     * 物品元数据
     */
    override var meta: VItemMeta,
) : RatzielItem