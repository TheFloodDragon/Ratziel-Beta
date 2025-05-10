package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.ItemData

/**
 * ItemInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/10 15:22
 */
interface ItemInterceptor {

    /**
     * 解释元素
     *
     * @param element 要解释的 [Element]
     * @param context 上下文
     * @return 物品数据 [ItemData]
     */
    fun intercept(element: Element, context: ArgumentContext): ItemData?

}