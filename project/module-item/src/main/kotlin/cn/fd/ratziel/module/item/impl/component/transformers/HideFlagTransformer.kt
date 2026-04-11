package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.impl.component.ItemStackTransformer
import cn.fd.ratziel.module.item.impl.component.type.HideFlag
import cn.fd.ratziel.module.item.util.modifyMeta
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * HideFlagTransformer
 *
 * 通过临时 RefItemStack + Bukkit ItemMeta 隐藏标签 API 适配 NbtTransformer。
 *
 * @author TheFloodDragon
 * @since 2026/4/11 22:36
 */
object HideFlagTransformer : ItemStackTransformer<Set<HideFlag>>() {

    override fun onWrite(item: ItemStack, component: Set<HideFlag>) = item.modifyMeta {
        clearHideFlags()
        for (flag in component) {
            addItemFlags(flag.get()!!)
        }
    }

    override fun onRead(item: ItemStack): Set<HideFlag>? {
        return item.itemMeta?.itemFlags?.map { HideFlag.of(it) }?.toSet()
    }

    override fun onRemove(item: ItemStack) = item.modifyMeta { clearHideFlags() }

    private fun ItemMeta.clearHideFlags() {
        removeItemFlags(*itemFlags.toTypedArray())
    }

}