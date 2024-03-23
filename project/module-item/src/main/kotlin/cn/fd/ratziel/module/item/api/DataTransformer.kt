package cn.fd.ratziel.module.item.api

import cn.fd.ratziel.module.item.nbt.NBTData

/**
 * DataTransformer - 物品数据转换器
 *
 * @author TheFloodDragon
 * @since 2024/3/16 10:48
 */
interface DataTransformer<T, D : NBTData> : Transformer<T, D> {

    /**
     * NBT数据的节点分配器
     */
    val node: NodeDistributor

}