package cn.fd.ratziel.module.item.feature.layer

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem

/**
 * ItemLayer - 物品图层
 *
 * @author TheFloodDragon
 * @since 2025/7/24 13:45
 */
interface ItemLayer {

    /**
     * 图层名称
     */
    val name: String

    /**
     * 图层数据
     */
    val data: ItemData

    /**
     * Renderer - 物品图层渲染器
     *
     * @author TheFloodDragon
     * @since 2025/7/24 13:50
     */
    interface Renderer {

        /**
         * 在物品 [item] 上渲染图层 [layer]
         *
         * @param item 物品实例
         * @param layer 图层实例
         */
        fun render(item: NeoItem, layer: ItemLayer)

    }

}