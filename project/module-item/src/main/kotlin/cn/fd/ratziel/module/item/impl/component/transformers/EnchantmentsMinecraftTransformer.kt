package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.api.component.transformer.MinecraftTransformer
import cn.fd.ratziel.module.item.impl.component.type.ItemEnchantmentMap
import cn.fd.ratziel.module.item.internal.RefItemStack

/**
 * EnchantmentsMinecraftTransformer
 *
 * 通过 Bukkit ItemMeta 附魔 API 读写原生附魔组件。
 *
 * @author TheFloodDragon
 * @since 2026/4/5 16:57
 */
@Suppress("unused")
object EnchantmentsMinecraftTransformer : MinecraftTransformer<ItemEnchantmentMap> {

    override fun read(nmsItem: Any): ItemEnchantmentMap? {
        val itemMeta = RefItemStack.ofNms(nmsItem).bukkitStack.itemMeta ?: return null
        val enchants = itemMeta.enchants
        if (enchants.isEmpty()) {
            return null
        }
        return ItemEnchantmentMap.fromBukkitMap(enchants)
    }

    override fun write(nmsItem: Any, component: ItemEnchantmentMap) {
        val bukkitStack = RefItemStack.ofNms(nmsItem).bukkitStack
        val itemMeta = bukkitStack.itemMeta ?: return
        itemMeta.removeEnchantments()
        component.toBukkitMap().forEach { (enchantment, level) ->
            itemMeta.addEnchant(enchantment, level, true)
        }
        bukkitStack.itemMeta = itemMeta
    }

    override fun remove(nmsItem: Any) {
        val bukkitStack = RefItemStack.ofNms(nmsItem).bukkitStack
        val itemMeta = bukkitStack.itemMeta ?: return
        if (itemMeta.enchants.isEmpty()) {
            return
        }
        itemMeta.removeEnchantments()
        bukkitStack.itemMeta = itemMeta
    }

}
