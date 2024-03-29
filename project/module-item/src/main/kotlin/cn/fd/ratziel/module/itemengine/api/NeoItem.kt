package cn.fd.ratziel.module.itemengine.api

import cn.fd.ratziel.module.itemengine.api.part.ItemData
import cn.fd.ratziel.module.itemengine.api.part.ItemInfo
import cn.fd.ratziel.module.item.impl.part.ItemMaterial
import cn.fd.ratziel.module.itemengine.api.part.ItemPart

/**
 * NeoItem - 物品
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:21
 */
interface NeoItem : ItemPart {

    /**
     * 物品材料
     */
    val material: ItemMaterial

    /**
     * 物品数据
     */
    val data: ItemData

    /**
     * 物品信息
     */
    val info: ItemInfo? get() = data[ItemInfo.NODE_ITEM]?.let { ItemInfo(it) }

}