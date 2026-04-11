package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.component.transformer.MinecraftTransformer
import cn.fd.ratziel.module.item.api.component.transformer.NbtTransformer
import cn.fd.ratziel.module.item.internal.RefItemStack
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * ItemStackTransformer
 * 
 * @author TheFloodDragon
 * @since 2026/4/11 22:19
 */
abstract class ItemStackTransformer<T> : NbtTransformer<T>, MinecraftTransformer<T> {

    protected val sandboxMaterial = Material.DIAMOND_SWORD

    abstract fun onWrite(item: ItemStack, component: T)
    abstract fun onRead(item: ItemStack): T?
    abstract fun onRemove(item: ItemStack)

    final override fun readFrom(root: NbtCompound): T? {
        return onRead(sandbox(root).bukkitStack) // 读取
    }

    final override fun writeTo(root: NbtCompound, component: T) {
        // 创建沙箱
        val sandbox = sandbox(root)
        // 写入
        onWrite(sandbox.bukkitStack, component)
        // 设置结果
        root.clear()
        root.putAll(sandbox.tag)
    }

    final override fun removeFrom(root: NbtCompound) {
        // 创建沙箱
        val sandbox = sandbox(root)
        // 删除
        onRemove(sandbox.bukkitStack)
        // 设置结果
        root.clear()
        root.putAll(sandbox.tag)
    }

    final override fun read(nmsItem: Any): T? = onRead(RefItemStack.ofNms(nmsItem).bukkitStack)
    final override fun write(nmsItem: Any, component: T) = onWrite(RefItemStack.ofNms(nmsItem).bukkitStack, component)
    final override fun remove(nmsItem: Any) = onRemove(RefItemStack.ofNms(nmsItem).bukkitStack)

    private fun sandbox(root: NbtCompound): RefItemStack {
        val sandbox = RefItemStack.of(ItemStack(sandboxMaterial))
        sandbox.tag = root
        return sandbox
    }

}