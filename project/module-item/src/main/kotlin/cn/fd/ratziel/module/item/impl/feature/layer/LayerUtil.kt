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
    fun render(item: NeoItem, name: String) {
        val layer = PhysicalLayer.getLayer(item.data, name)
            ?: throw IllegalArgumentException("Layer '$name' not found in item $item!")
        PhysicalLayer.Renderer.render(item, layer)
    }

}