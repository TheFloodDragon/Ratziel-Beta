package cn.fd.ratziel.module.item.impl.component.transformers

import cn.fd.ratziel.module.item.impl.component.ItemStackTransformer
import cn.fd.ratziel.module.item.impl.component.type.ItemEnchantmentMap
import cn.fd.ratziel.module.item.util.modifyMeta
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * EnchantmentsTransformer
 *
 * 通过 Bukkit ItemMeta 附魔 API 读写原生附魔组件。
 *
 * @author TheFloodDragon
 * @since 2026/4/11 22:30
 */
object EnchantmentsTransformer : ItemStackTransformer<ItemEnchantmentMap>() {

    override fun onWrite(item: ItemStack, component: ItemEnchantmentMap) = item.modifyMeta {
        clearEnchantments()
        component.toBukkitMap().forEach { (enchantment, level) ->
            addEnchant(enchantment, level, true)
        }
    }

    override fun onRead(item: ItemStack): ItemEnchantmentMap? {
        return item.itemMeta?.enchants?.let { ItemEnchantmentMap.fromBukkitMap(it) }
    }

    override fun onRemove(item: ItemStack) = item.modifyMeta { clearEnchantments() }

    private fun ItemMeta.clearEnchantments() {
        try {
            removeEnchantments()
        } catch (_: NoSuchMethodError) {
            enchants.keys.toList().forEach(::removeEnchant)
        }
    }

}
