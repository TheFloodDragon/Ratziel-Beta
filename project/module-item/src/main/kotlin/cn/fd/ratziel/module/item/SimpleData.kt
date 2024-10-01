package cn.fd.ratziel.module.item

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.nbt.NBTCompound

/**
 * SimpleData
 *
 * @author TheFloodDragon
 * @since 2024/5/5 13:33
 */
data class SimpleData(
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

    /**
     * 克隆数据
     */
    override fun clone() = this.copy()

    /**
     * 合并数据
     */
    override fun merge(other: ItemData) {
        if (other.material != ItemMaterial.EMPTY) this.material = other.material
        if (other.amount >= 1) this.amount = other.amount
    }

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