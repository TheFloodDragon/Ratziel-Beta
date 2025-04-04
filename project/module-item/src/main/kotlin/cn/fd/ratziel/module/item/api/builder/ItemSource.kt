package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem

/**
 * ItemSource
 *
 * @author TheFloodDragon
 * @since 2025/4/4 14:57
 */
interface ItemSource {

    /**
     * 生成基本物品
     *
     * @param element 物品元素
     * @param sourceData 物品源数据
     * @param context 上下文
     */
    fun generateItem(element: Element, sourceData: ItemData, context: ArgumentContext): NeoItem?

}