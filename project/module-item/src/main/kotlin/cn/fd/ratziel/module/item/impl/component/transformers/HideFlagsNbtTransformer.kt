package cn.fd.ratziel.module.item.impl.component.transformers

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.component.transformer.NbtTransformer
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import cn.fd.ratziel.module.item.impl.component.HideFlag
import cn.fd.ratziel.module.item.internal.RefItemStack
import cn.fd.ratziel.module.item.util.MetaMatcher
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import java.util.LinkedHashSet

/**
 * HideFlagsNbtTransformer
 *
 * 通过临时 RefItemStack + Bukkit ItemMeta 隐藏标签 API 适配 NbtTransformer。
 *
 * @author TheFloodDragon
 * @since 2026/4/6 19:28
 */
object HideFlagsNbtTransformer : NbtTransformer<Set<HideFlag>> {

    override fun writeTo(root: NbtCompound, component: Set<HideFlag>) {
        mutateRoot(root) { sandbox ->
            val itemMeta = sandbox.bukkitStack.itemMeta ?: return@mutateRoot
            itemMeta.clearItemFlags()
            val flags = component.toBukkitArray()
            if (flags.isNotEmpty()) {
                itemMeta.addItemFlags(*flags)
            }
            sandbox.bukkitStack.itemMeta = itemMeta
        }
    }

    override fun readFrom(root: NbtCompound): Set<HideFlag>? {
        val itemMeta = sandbox(root).bukkitStack.itemMeta ?: return null
        val flags = itemMeta.itemFlags
        if (flags.isEmpty()) {
            return null
        }
        return fromBukkitSet(flags)
    }

    override fun removeFrom(root: NbtCompound) {
        mutateRoot(root) { sandbox ->
            val itemMeta = sandbox.bukkitStack.itemMeta ?: return@mutateRoot
            if (itemMeta.itemFlags.isEmpty()) {
                return@mutateRoot
            }
            itemMeta.clearItemFlags()
            sandbox.bukkitStack.itemMeta = itemMeta
        }
    }

    private fun sandbox(root: NbtCompound): RefItemStack {
        return RefItemStack.of(SimpleData(material = sandboxMaterial, tag = root.clone()))
    }

    private inline fun mutateRoot(root: NbtCompound, action: (RefItemStack) -> Unit) {
        val sandbox = sandbox(root)
        action(sandbox)
        root.clear()
        root.putAll(sandbox.tag)
    }

    private fun ItemMeta.clearItemFlags() {
        val flags = itemFlags.toTypedArray()
        if (flags.isNotEmpty()) {
            removeItemFlags(*flags)
        }
    }

    private fun Set<HideFlag>.toBukkitArray(): Array<ItemFlag> {
        return mapNotNull { it.get() }.toTypedArray()
    }

    private fun fromBukkitSet(source: Set<ItemFlag>): Set<HideFlag> {
        return LinkedHashSet<HideFlag>(source.size).apply {
            source.forEach { flag ->
                add(MetaMatcher.matchHideFlag(flag.name))
            }
        }
    }

    /**
     * 仅作为隐藏标签 NBT 读写的临时载体使用，不影响最终写回的根标签。
     */
    private val sandboxMaterial = SimpleMaterial("DIAMOND_SWORD")

}
