package cn.fd.ratziel.module.item.util

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.platform.util.BukkitSkull
import java.util.concurrent.ConcurrentHashMap

/**
 * SkullUtil
 *
 * @author TheFloodDragon
 * @since 2025/5/25 08:57
 */
object SkullUtil {

    /**
     * 头颅缓存
     */
    private val CACHE: MutableMap<String, ItemStack> = ConcurrentHashMap()

    /**
     * 获取头颅数据
     */
    fun fetchSkull(value: String): ItemStack {
        return CACHE.computeIfAbsent(value.trim()) { generateSkullItem(it) }.clone()
    }

    /**
     * 生成纯头颅数据的 [ItemStack]
     */
    fun generateSkullItem(value: String): ItemStack {
        return BukkitSkull.applySkull(value)
    }

    /**
     * 读取头颅数据
     */
    fun getSkullValue(skullMeta: SkullMeta): String {
        return BukkitSkull.getSkullValue(skullMeta)
    }

    /**
     * 读取头颅数据
     */
    fun getSkullValue(itemStack: ItemStack): String? {
        val meta = itemStack.itemMeta as? SkullMeta ?: return null
        return getSkullValue(meta)
    }

}