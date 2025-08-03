package cn.fd.ratziel.module.item.feature.virtual

import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.module.item.api.NeoItem

/**
 * VirtualItemRenderer
 *
 * @author TheFloodDragon
 * @since 2025/8/3 11:37
 */
interface VirtualItemRenderer {

    /**
     * 渲染虚拟客户端物品实例
     *
     * @param actual 实际物品实例
     * @param context 参数上下文
     */
    fun render(actual: NeoItem, context: ArgumentContext)

    /**
     * 尝试从实际物品的数据中直接渲染虚拟物品
     *
     * @param actual 实际物品实例
     */
    fun renderBySelf(actual: NeoItem)

    /**
     * 尝试从虚拟物品中恢复实际物品
     *
     * @param virtual 虚拟物品实例
     * @return 恢复后的实际物品实例
     */
    fun recover(virtual: NeoItem)

    /**
     * Acceptor - 用于处理虚拟物品数据
     */
    interface Acceptor {

        /**
         * 接收实际物品并处理成虚拟物品
         *
         * @param actual 实际物品
         * @param context 参数上下文
         */
        fun accept(actual: NeoItem, context: ArgumentContext)

    }

}