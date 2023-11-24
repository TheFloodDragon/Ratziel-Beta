package cn.fd.ratziel.module.itemengine.api

import cn.fd.ratziel.module.itemengine.api.meta.ItemMetadata

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
     * 物品元数据
     */
    val meta: ItemMetadata
}