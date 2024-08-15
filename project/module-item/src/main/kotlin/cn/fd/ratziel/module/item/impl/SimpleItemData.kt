package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.nbt.NBTCompound

/**
 * SimpleItemData
 *
 * @author TheFloodDragon
 * @since 2024/5/5 13:33
 */
data class SimpleItemData(
    /**
     * 物品材料
     */
    override var material: ItemMaterial = ItemMaterial.EMPTY,
    /**
     * 物品NBT
     */
    override var tag: NBTCompound = NBTCompound(),
    /**
     * 物品数量
     */
    override var amount: Int = 1
) : ItemData {

    companion object {

        /**
         * 将 [target] 合并到 [source] 中
         */
        @Deprecated("Shit")
        fun merge(source: ItemData, target: ItemData, replace: Boolean = true) {
            mergeWithoutTag(source, target)
            source.tag.merge(target.tag, replace)
        }

        /**
         * 将 [target] 合并到 [source] 中 (不合并[tag])
         */
        @Deprecated("Shit")
        fun mergeWithoutTag(source: ItemData, target: ItemData) {
            if (target.material != ItemData.EMPTY.material) source.material = target.material
            if (target.amount != ItemData.EMPTY.amount) source.amount = target.amount
        }

        /**
         * 将 [target] 合并到 [source] 中 (浅合并)
         */
        @Deprecated("Shit")
        fun mergeShallow(source: ItemData, target: ItemData, replace: Boolean = true) {
            mergeWithoutTag(source, target)
            source.tag.mergeShallow(target.tag, replace)
        }

    }

}