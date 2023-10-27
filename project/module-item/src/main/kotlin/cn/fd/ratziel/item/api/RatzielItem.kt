package cn.fd.ratziel.item.api

import cn.fd.ratziel.item.api.meta.ItemMetadata

/**
 * RatzielItem - Ratziel 物品
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:21
 */
interface RatzielItem {
    /**
     * 物品元数据
     */
    val meta: ItemMetadata
}