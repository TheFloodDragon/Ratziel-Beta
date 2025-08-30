package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.item.api.NeoItem

/**
 * ItemSource - 物品源
 *
 * @author TheFloodDragon
 * @since 2025/4/4 14:57
 */
interface ItemSource {

    /**
     * 生成基本物品
     *
     * @param element 物品元素
     * @param context 上下文
     */
    fun generateItem(element: Element, context: ArgumentContext): NeoItem?

    /**
     * Named
     *
     * @author TheFloodDragon
     * @since 2025/6/9 19:19
     */
    interface Named : ItemSource {

        /**
         * 物品源名称
         */
        val names: Array<String>

    }

}