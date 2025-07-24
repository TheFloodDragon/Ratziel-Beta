package cn.fd.ratziel.module.item.impl.feature.layer

import cn.fd.ratziel.module.item.api.NeoItem

/**
 * LayerUtil
 *
 * @author TheFloodDragon
 * @since 2025/7/24 14:55
 */
object LayerUtil {

    /**
     * 渲染物品图层
     */
    @JvmStatic
    fun render(item: NeoItem, layerName: String) {
        val layer = PhysicalLayer.getLayer(item.data, layerName)
            ?: throw IllegalArgumentException("Layer '$layerName' not found in item $item")
        PhysicalLayer.Renderer.render(item, layer)
    }

}