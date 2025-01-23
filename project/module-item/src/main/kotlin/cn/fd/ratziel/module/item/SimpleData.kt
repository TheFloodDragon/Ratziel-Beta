package cn.fd.ratziel.module.item

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import java.util.concurrent.ConcurrentHashMap

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
    override var tag: NbtCompound = NbtCompound(ConcurrentHashMap()),
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
        this.tag.merge(other.tag)
    }

}