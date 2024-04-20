package cn.fd.ratziel.module.item.api

import cn.fd.ratziel.module.item.nbt.NBTCompound

/**
 * ItemComponent - 物品组件
 *
 * @author TheFloodDragon
 * @since 2024/4/20 9:14
 */
interface ItemComponent : Transformable<NBTCompound> {

    /**
     * NBT数据的节点分配器
     */
    fun node(): NodeDistributor

}