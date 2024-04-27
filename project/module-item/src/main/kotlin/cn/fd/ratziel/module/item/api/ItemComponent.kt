package cn.fd.ratziel.module.item.api

import cn.fd.ratziel.module.item.nbt.NBTCompound

/**
 * ItemComponent - 物品组件
 *
 * @author TheFloodDragon
 * @since 2024/4/20 9:14
 */
interface ItemComponent : Transformable<ItemData> {

    /**
     * 获取数据的节点分配器
     */
    fun getNode(): NodeDistributor

    /**
     * 正向转化 - 输出型转化
     * @param source 源标签
     */
    fun transform(source: ItemData): ItemData

    /**
     * 正向转化 - 输出型转化
     * 重写并调用 [transform] 方法, 默认使用空的 [NBTCompound] 作为源数据
     */
    override fun transform(): ItemData = transform(ItemData())

}