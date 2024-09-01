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
    var material: ItemMaterial

    /**
     * 物品总标签数据
     */
    var tag: NBTCompound

    /**
     * 物品数量
     */
    var amount: Int

    companion object {

        /**
         * 空 [ItemData]
         */
        val EMPTY = object : ItemData {
            override var material: ItemMaterial
                get() = ItemMaterial.EMPTY
                set(_) = throw UnsupportedOperationException("ItemData#EMPTY cannot be changed")
            override var tag: NBTCompound
                get() = NBTCompound()
                set(_) = throw UnsupportedOperationException("ItemData#EMPTY cannot be changed")
            override var amount: Int
                get() = 0
                set(_) = throw UnsupportedOperationException("ItemData#EMPTY cannot be changed")
        }

    }

}