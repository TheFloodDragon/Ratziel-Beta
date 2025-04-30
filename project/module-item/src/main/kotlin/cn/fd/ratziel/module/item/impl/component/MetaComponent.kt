package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * MetaComponent
 *
 * 基于 Bukkit [ItemMeta] 的组件
 *
 * @author TheFloodDragon
 * @since 2025/4/30 19:31
 */
open class MetaComponent<T : ItemMeta>(itemStack: ItemStack?) {

    private val ref: RefItemStack? = itemStack?.let { RefItemStack.of(it) }

    /**
     * [ItemMeta]
     */
    var meta: T?
        get() {
            @Suppress("UNCHECKED_CAST")
            return ref?.bukkitStack?.itemMeta as? T ?: throw IllegalStateException("Invalid meta!")
        }
        set(value) {
            ref?.bukkitStack?.itemMeta = value
        }

    /**
     * 缓存的物品标签 (克隆)
     */
    val tag: NbtCompound
        get() {
            return ref?.tag ?: NbtCompound()
        }

}
