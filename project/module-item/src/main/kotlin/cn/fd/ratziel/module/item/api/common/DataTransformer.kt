package cn.fd.ratziel.module.item.api.common

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NodeDistributor

/**
 * DataTransformer
 *
 * @author TheFloodDragon
 * @since 2024/4/27 10:01
 */
interface DataTransformer<T> {

    /**
     * NBT数据的节点分配器
     */
    val node: NodeDistributor

    /**
     * 正向转化 - 输出型转化
     * @param source 源标签
     */
    fun transform(target: T, source: ItemData): ItemData

    /**
     * 反向转化 - 应用型转换
     */
    fun detransform(target: T, from: ItemData)

}