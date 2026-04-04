package cn.fd.ratziel.module.item.util

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.component.ItemComponentData
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.internal.RefItemStack
import org.bukkit.inventory.ItemStack

/**
 * 将 [NeoItem] 转化成 [ItemStack]
 */
fun NeoItem.toItemStack(): ItemStack {
    return RefItemStack.of(this.data).bukkitStack
}

/**
 * 将 [NeoItem] 的数据写入目标 [itemStack]
 */
fun NeoItem.writeTo(itemStack: ItemStack) {
    return RefItemStack.of(this.data).writeTo(itemStack)
}

/**
 * 将 [ItemData] 转化成 [ItemComponentData]
 */
fun ItemData.asComponentData(): ItemComponentData = this as? ItemComponentData ?: ItemComponentDataProxy(this)

/**
 * 将 [ItemStack] 转化成代理数据体 [ItemComponentData]
 */
fun ItemStack.asItemData(): ItemComponentData = RefItemStack.of(this)

/**
 * 提取 [ItemStack] 中的数据
 */
fun ItemStack.extractData(): ItemData = RefItemStack.of(this).extractData()



private class ItemComponentDataProxy(
    private val data: ItemData,
) : ItemComponentData {
    override var material: ItemMaterial by data::material
    override var tag: NbtCompound by data::tag
    override var amount: Int by data::amount
    override fun <T : Any> get(type: ItemComponentType<T>): T? = type.transforming.nbtTransformer.readFrom(tag)
    override fun <T : Any> set(type: ItemComponentType<T>, value: T) = type.transforming.nbtTransformer.writeTo(tag, value)
    override fun remove(type: ItemComponentType<*>) = type.transforming.nbtTransformer.removeFrom(tag)
    override fun clone() = ItemComponentDataProxy(this.data.clone())
}
