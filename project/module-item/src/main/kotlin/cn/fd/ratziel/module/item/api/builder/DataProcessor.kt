package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.module.item.api.ItemData

/**
 * DataProcessor - 物品数据处理器
 *
 * 用于在组件序列化完成后的数据处理
 *
 * @author TheFloodDragon
 * @since 2025/3/22 20:48
 */
interface DataProcessor {

    /**
     * 处理组件数据
     * @param data 组件数据 (并不是整体数据)
     */
    fun process(data: ItemData): ItemData

    /**
     * 无处理
     */
    object NoProcess : DataProcessor {
        override fun process(data: ItemData) = data
    }

}