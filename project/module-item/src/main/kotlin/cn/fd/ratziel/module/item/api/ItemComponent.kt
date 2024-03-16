package cn.fd.ratziel.module.item.api

import cn.fd.ratziel.module.item.api.common.DataTransformer
import cn.fd.ratziel.module.item.nbt.NBTData

/**
 * ItemComponent - 物品组件
 *
 * @author TheFloodDragon
 * @since 2024/3/15 18:53
 */
interface ItemComponent<T, D : NBTData> {

    /**
     * 获取节点分配器
     */
    fun getNodeDistributor(): NodeDistributor

    /**
     * 获取数据转换器
     */
    fun getTransformer(): DataTransformer<T, D>

    //TODO ItemComponent AnyThing else
}