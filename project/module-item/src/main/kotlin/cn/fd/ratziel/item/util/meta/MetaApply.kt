package cn.fd.ratziel.item.util.meta

import cn.fd.ratziel.item.api.meta.ItemCharacteristic
import cn.fd.ratziel.item.api.meta.ItemDisplay
import com.google.common.collect.LinkedHashMultimap
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.module.nms.MinecraftVersion
import java.util.function.Consumer

/**
 * ItemDisplay -> Bukkit.ItemMeta
 */
fun ItemDisplay.applyTo(
    target: ItemMeta, replace: Boolean = false,
    fDisplayName: Consumer<ItemDisplay> = Consumer {
        if (it.name != null || replace)
            target.setProperty("displayName", nmsComponent(it.name))
    },
    fLore: Consumer<ItemDisplay> = Consumer {
        if (it.lore != null || replace)
            target.setProperty("lore", it.lore?.mapNotNull { c -> nmsComponent(c) })
    },
    fLocalName: Consumer<ItemDisplay> = Consumer {
        if (it.localizedName != null || replace)
            target.setProperty("locName", nmsComponent(it.localizedName))
    },
) {
    fDisplayName.accept(this)
    fLore.accept(this)
    fLocalName.accept(this)
}

/**
 * ItemCharacteristic -> Bukkit.ItemMeta
 */
fun ItemCharacteristic.applyTo(
    target: ItemMeta, replace: Boolean = false,
    /**
     * 值的设置
     * 使用变量的形式是为了自定义性
     */
    fCustomModelData: Consumer<ItemCharacteristic> = Consumer {
        if (it.customModelData != null || replace)
            target.setProperty("customModelData", it.customModelData)
    },
    fEnchants: Consumer<ItemCharacteristic> = Consumer {
        if (it.enchants != null || replace) {
            target.setProperty("enchantments", null)
            it.enchants?.forEach { (key, value) ->
                target.addEnchant(key, value, true)
            }
        }
    },
    fItemFlags: Consumer<ItemCharacteristic> = Consumer {
        if (it.hideFlags != null || replace) {
            //target.setProperty("hideFlag", null)
            target.removeItemFlags(*target.itemFlags.toTypedArray())
            target.addItemFlags(*it.hideFlags?.toTypedArray() ?: emptyArray())
        }
    },
    fUnbreakable: Consumer<ItemCharacteristic> = Consumer {
        if (it.unbreakable != null || replace)
            target.isUnbreakable = it.unbreakable == true
    },
    fAttributeModifiers: Consumer<ItemCharacteristic> = Consumer {
        if (it.attributeModifiers != null || replace)
            target.attributeModifiers = LinkedHashMultimap.create<Attribute, AttributeModifier>().apply {
                it.attributeModifiers?.forEach { (key, values) ->
                    putAll(key, values)
                }
            }
    },
) {
    if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_14))
        fCustomModelData.accept(this)
    fEnchants.accept(this)
    fItemFlags.accept(this)
    fUnbreakable.accept(this)
    fAttributeModifiers.accept(this)
}