package cn.fd.ratziel.module.item.impl.component.transformers

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.component.transformer.NbtTransformer
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import cn.fd.ratziel.module.item.impl.component.type.ItemEnchantmentMap
import cn.fd.ratziel.module.item.internal.RefItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * EnchantmentsNbtTransformer
 *
 * 通过临时 [RefItemStack] + Bukkit [ItemMeta] 附魔 API 适配 [NbtTransformer]。
 *
 * 不再直接解析各版本的附魔 NBT 结构，而是交给 Bukkit/服务端完成附魔组件的读写与删除。
 *
 * @author TheFloodDragon
 * @since 2026/4/6 02:31
 */
object EnchantmentsNbtTransformer : NbtTransformer<ItemEnchantmentMap> {

    override fun writeTo(root: NbtCompound, component: ItemEnchantmentMap) {
        mutateRoot(root) { sandbox ->
            val itemMeta = sandbox.bukkitStack.itemMeta ?: return@mutateRoot
            itemMeta.clearEnchantments()
            component.toBukkitMap().forEach { (enchantment, level) ->
                itemMeta.addEnchant(enchantment, level, true)
            }
            sandbox.bukkitStack.itemMeta = itemMeta
        }
    }

    override fun readFrom(root: NbtCompound): ItemEnchantmentMap? {
        val itemMeta = sandbox(root).bukkitStack.itemMeta ?: return null
        val enchants = itemMeta.enchants
        if (enchants.isEmpty()) {
            return null
        }
        return ItemEnchantmentMap.fromBukkitMap(enchants)
    }

    override fun removeFrom(root: NbtCompound) {
        mutateRoot(root) { sandbox ->
            val itemMeta = sandbox.bukkitStack.itemMeta ?: return@mutateRoot
            if (itemMeta.enchants.isEmpty()) {
                return@mutateRoot
            }
            itemMeta.clearEnchantments()
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

    private fun ItemMeta.clearEnchantments() {
        try {
            removeEnchantments()
        } catch (_: NoSuchMethodError) {
            enchants.keys.toList().forEach(::removeEnchant)
        }
    }

    /**
     * 仅作为附魔 NBT 读写的临时载体使用，不影响最终写回的根标签。
     */
    private val sandboxMaterial = SimpleMaterial("DIAMOND_SWORD")

}
