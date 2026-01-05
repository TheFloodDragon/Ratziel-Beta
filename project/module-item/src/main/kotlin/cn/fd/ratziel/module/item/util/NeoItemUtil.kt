package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
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
 * 将 [ItemStack] 转化成代理数据体 [ItemData]
 */
fun ItemStack.asItemData(): ItemData = RefItemStack.of(this)

/**
 * 提取 [ItemStack] 中的数据
 */
fun ItemStack.extractData(): ItemData  = RefItemStack.of(this).extractData()
