package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.nbt.NBTCompound

/**
 * TheItemData
 *
 * @author TheFloodDragon
 * @since 2024/5/5 13:33
 */
data class TheItemData(
    /**
     * 物品材料
     */
    override var material: ItemMaterial = ItemMaterial.EMPTY,
    /**
     * 物品NBT
     */
    override val tag: NBTCompound = NBTCompound(),
    /**
     * 物品数量
     */
    override var amount: Int = 1
) : ItemData {

    companion object {

        val EMPTY = TheItemData()

        /**
         * 将 [target] 合并到 [source] 中 (不合并[tag])
         */
        fun mergeWithoutTag(source: ItemData, target: ItemData) {
            if (target.material != EMPTY.material) source.material = target.material
            if (target.amount != EMPTY.amount) source.amount = target.amount
        }

        /**
         * 将 [target] 合并到 [source] 中 (浅合并)
         */
        fun mergeShallow(source: ItemData, target: ItemData, replace: Boolean = true) {
            mergeWithoutTag(source, target)
            source.tag.mergeShallow(target.tag, replace)
        }

    }

}