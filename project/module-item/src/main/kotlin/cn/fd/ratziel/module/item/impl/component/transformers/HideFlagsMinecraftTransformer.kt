package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.api.component.transformer.MinecraftTransformer
import cn.fd.ratziel.module.item.impl.component.HideFlag
import cn.fd.ratziel.module.item.internal.RefItemStack
import cn.fd.ratziel.module.item.util.MetaMatcher
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import java.util.LinkedHashSet

/**
 * HideFlagsMinecraftTransformer
 *
 * 通过 Bukkit ItemMeta 隐藏标签 API 读写原生隐藏标签组件。
 *
 * @author TheFloodDragon
 * @since 2026/4/6 19:27
 */
@Suppress("unused")
object HideFlagsMinecraftTransformer : MinecraftTransformer<Set<HideFlag>> {

    override fun read(nmsItem: Any): Set<HideFlag>? {
        val itemMeta = RefItemStack.ofNms(nmsItem).bukkitStack.itemMeta ?: return null
        val flags = itemMeta.itemFlags
        if (flags.isEmpty()) {
            return null
        }
        return fromBukkitSet(flags)
    }

    override fun write(nmsItem: Any, component: Set<HideFlag>) {
        val bukkitStack = RefItemStack.ofNms(nmsItem).bukkitStack
        val itemMeta = bukkitStack.itemMeta ?: return
        itemMeta.clearItemFlags()
        val flags = component.toBukkitArray()
        if (flags.isNotEmpty()) {
            itemMeta.addItemFlags(*flags)
        }
        bukkitStack.itemMeta = itemMeta
    }

    override fun remove(nmsItem: Any) {
        val bukkitStack = RefItemStack.ofNms(nmsItem).bukkitStack
        val itemMeta = bukkitStack.itemMeta ?: return
        if (itemMeta.itemFlags.isEmpty()) {
            return
        }
        itemMeta.clearItemFlags()
        bukkitStack.itemMeta = itemMeta
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

}
