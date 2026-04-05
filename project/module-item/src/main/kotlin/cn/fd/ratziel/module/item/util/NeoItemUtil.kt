package cn.fd.ratziel.module.item.util

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.component.ItemComponentData
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.impl.SimpleData
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
 * 将 [ItemStack] 转化成代理数据体 [ItemComponentData]
 */
fun ItemStack.asItemData(): ItemComponentData = RefItemStack.of(this)

/**
 * 提取 [ItemStack] 中的数据 (纯 NBT标签 数据)
 */
fun ItemStack.extractData(): ItemData = RefItemStack.of(this).extractData()

/**
 * 修改 NBT 标签
 *
 * 建议所有对标签的修改都通过这个方法进行，以保证在需要写回数据时能够正确写回。
 */
fun ItemData.modifyTag(action: (NbtCompound) -> Unit) {
    // 判断 ItemData 是否为纯数据体
    if (this is SimpleData) { // 没想到什么好的办法, 反正纯数据体基本也就它一个, 就这样用着吧
        action(this.tag) // 直接修改标签而不需要写回
    } else {
        val handle = this.tag // 获取标签的副本
        action(handle) // 处理临时数据
        this.tag = handle // 需要写回数据
    }
}

/**
 * 将 [ItemData] 转化成 [ItemComponentData]
 */
fun ItemData.asComponentData(): ItemComponentData = this as? ItemComponentData ?: ItemComponentDataProxy(this)

private class ItemComponentDataProxy(
    private val data: ItemData,
) : ItemComponentData {
    override var material: ItemMaterial by data::material
    override var tag: NbtCompound by data::tag
    override var amount: Int by data::amount
    override fun <T : Any> get(type: ItemComponentType<T>): T? = type.transforming.nbtTransformer.readFrom(tag)
    override fun <T : Any> set(type: ItemComponentType<T>, value: T) = modifyTag { type.transforming.nbtTransformer.writeTo(it, value) }
    override fun remove(type: ItemComponentType<*>) = modifyTag { type.transforming.nbtTransformer.removeFrom(it) }
    override fun clone() = ItemComponentDataProxy(this.data.clone())
}
