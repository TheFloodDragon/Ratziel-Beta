package cn.fd.ratziel.module.item.api

import cn.fd.ratziel.module.nbt.NBTCompound

/**
 * ItemData - 物品数据
 *
 * @author TheFloodDragon
 * @since 2024/4/27 09:35
 */
interface ItemData {

    /**
     * 物品材料
     */
    val material: ItemMaterial

    /**
     * 物品NBT
     */
    val tag: NBTCompound

    /**
     * 物品数量
     */
    val amount: Int

    /**
     * 可变的 [ItemData]
     */
    interface Mutable : ItemData {

        /**
         * 物品材料 (可变)
         */
        override var material: ItemMaterial

        /**
         * 物品标签数据
         */
        override val tag: NBTCompound

        /**
         * 物品数量 (可变)
         */
        override var amount: Int

    }

    companion object {

        /**
         * 空 [ItemData]
         */
        val EMPTY = object : ItemData {
            override val material = ItemMaterial.EMPTY
            override val tag: NBTCompound = NBTCompound()
            override var amount = 0
        }

    }

}