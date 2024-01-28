package cn.fd.ratziel.module.itemengine.item

import cn.fd.ratziel.module.itemengine.api.NeoItem
import cn.fd.ratziel.module.itemengine.api.part.ItemData
import cn.fd.ratziel.module.itemengine.api.part.ItemMaterial

/**
 * RatzielItem - Ratziel 物品
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:10
 */
data class RatzielItem(
    /**
     * 物品材质
     */
    override val material: ItemMaterial,
    /**
     * 物品数据
     */
    override val data: ItemData,
) : NeoItem