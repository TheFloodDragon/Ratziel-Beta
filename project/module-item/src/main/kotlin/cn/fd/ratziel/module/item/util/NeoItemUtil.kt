package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import org.bukkit.inventory.ItemStack


/**
 * 将 [NeoItem] 转化成 [ItemStack]
 */
fun NeoItem.toItemStack(): ItemStack {
    return RefItemStack.of(this.data).bukkitStack
}