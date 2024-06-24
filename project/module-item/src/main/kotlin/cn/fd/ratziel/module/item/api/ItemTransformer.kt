package cn.fd.ratziel.module.item.api

/**
 * ItemTransformer
 *
 * @author TheFloodDragon
 * @since 2024/6/24 13:59
 */
interface ItemTransformer<T> {

    /**
     * 物品节点
     */
    val node: ItemNode

    /**
     * 正向转化 - 输出型转换
     */
    fun transform(component: T): ItemData

    /**
     * 反向转化 - 应用型转换
     */
    fun detransform(data: ItemData): T

}