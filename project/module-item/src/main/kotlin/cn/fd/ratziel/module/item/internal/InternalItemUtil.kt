package cn.fd.ratziel.module.item.internal

import cn.altawk.nbt.tag.NbtCompound
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * InternalItemUtil
 * 
 * @author TheFloodDragon
 * @since 2026/4/11 22:07
 */

internal fun NbtCompound.sandbox(): ItemStack {
    val sandbox = RefItemStack.of(ItemStack(Material.DIAMOND_SWORD))
    sandbox.tag = this
    return sandbox.bukkitStack
}

internal fun NbtCompound.mutate(action: ItemStack.() -> Unit) {
    val sandbox = RefItemStack.of(ItemStack(Material.DIAMOND_SWORD))
    sandbox.tag = this
    action(sandbox.bukkitStack)
    this.clear()
    this.putAll(sandbox.tag)
}
