@file:Suppress("DEPRECATION")

package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import org.bukkit.Material
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import taboolib.common.util.Strings
import taboolib.library.xseries.XAttribute
import taboolib.library.xseries.XEnchantment
import taboolib.library.xseries.XItemFlag
import taboolib.library.xseries.XMaterial
import taboolib.type.BukkitEquipment
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

/**
 * MetaMatcher
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:07
 */
object MetaMatcher {

    /**
     * 匹配物品魔咒
     */
    @JvmStatic
    fun matchEnchantment(source: String): XEnchantment {
        val name = trim(source)
        return XEnchantment.of(name).getOrElse { // XSeries match
            (Enchantment.getByName(name)  // Bukkit match
                ?: Enchantment.values().maxBy { Strings.similarDegree(it.name, name) }) // Similar
                .let { XEnchantment.of(it) }
        }
    }

    /**
     * 匹配穿戴栏位
     */
    @JvmStatic
    fun matchEquipment(source: String): BukkitEquipment {
        val name = trim(source)
        return BukkitEquipment.fromString(name)
            ?: BukkitEquipment.entries.maxBy { Strings.similarDegree(it.name, name) }
    }

    /**
     * 匹配物品属性修饰符
     */
    @JvmStatic
    fun matchAttribute(source: String): XAttribute {
        val name = trim(source)
        return XAttribute.of(name).getOrNull()
            ?: XAttribute.getValues().maxBy { Strings.similarDegree(it.name(), name) }
    }

    /**
     * 匹配属性操作符
     */
    @JvmStatic
    fun matchAttributeOperation(source: String): AttributeModifier.Operation {
        val name = trim(source)
        return try {
            AttributeModifier.Operation.valueOf(name)
        } catch (_: IllegalArgumentException) {
            AttributeModifier.Operation.entries.maxBy { Strings.similarDegree(it.name, name) }
        }
    }

    /**
     * 匹配物品(隐藏)标签
     */
    @JvmStatic
    fun matchHideFlag(source: String): XItemFlag {
        val name = trim(source)
        return XItemFlag.of(name).getOrElse {
            XItemFlag.entries.maxBy { Strings.similarDegree(it.name, name) }
        }
    }

    /**
     * 匹配物品材料
     */
    @JvmStatic
    fun matchMaterial(source: String): ItemMaterial {
        val name = trim(source)
        // 尝试匹配准确材料
        val matched: Material? =
            XMaterial.matchXMaterial(name).getOrNull()?.get() // XMaterial Match
                ?: Material.getMaterial(name) // BukkitMaterial Match
        return if (matched != null) SimpleMaterial(matched)
        else SimpleMaterial.materialsMap.maxBy { Strings.similarDegree(it.key, source) }.value // Similar
    }

    private fun trim(source: String): String = source.uppercase().replace(" ", "_").replace('-', '_')

}