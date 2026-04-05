package cn.fd.ratziel.module.item.impl

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.component.ItemComponentData
import cn.fd.ratziel.module.item.api.component.ItemComponentType

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
    override var amount: Int = 1,
) : ItemComponentData {

    override fun <T : Any> get(type: ItemComponentType<T>): T? = type.transforming.nbtTransformer.readFrom(tag)
    override fun <T : Any> set(type: ItemComponentType<T>, value: T) = type.transforming.nbtTransformer.writeTo(tag, value)
    override fun remove(type: ItemComponentType<*>) = type.transforming.nbtTransformer.removeFrom(tag)

    /**
     * 克隆数据
     */
    override fun clone() = SimpleData(this.material, this.tag.clone(), this.amount)

}
