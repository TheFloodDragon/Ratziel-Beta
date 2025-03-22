package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.module.item.api.ItemData

/**
 * DataProcessor
 *
 * @author TheFloodDragon
 * @since 2025/3/22 20:48
 */
interface DataProcessor {

    /**
     * 处理数据
     * @param data 数据
     */
    fun process(data: ItemData): ItemData

    /**
     * 无处理
     */
    object NoProcess : DataProcessor {
        override fun process(data: ItemData) = data
    }

}