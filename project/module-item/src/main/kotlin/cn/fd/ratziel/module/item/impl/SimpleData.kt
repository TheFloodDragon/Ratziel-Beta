package cn.fd.ratziel.module.item.impl

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial

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
     * 物品标签
     */
    override var tag: NbtCompound = NbtCompound(),
    /**
     * 物品数量
     */
    override var amount: Int = 1
) : ItemData {

    /**
     * 克隆数据
     */
    override fun clone() = this.copy()

}