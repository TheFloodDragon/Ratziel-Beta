package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.module.item.api.ItemData

/**
 * ItemTransformer
 *
 * @author TheFloodDragon
 * @since 2024/6/24 13:59
 */
interface ItemTransformer<T> {

    /**
     * 正向转化 - 将 [component] 的内容合并到 [data] 中
     */
    fun transform(data: ItemData.Mutable, component: T)

    /**
     * 反向转化 - 通过 [data] 构造组件
     */
    fun detransform(data: ItemData): T

}