@file:Suppress("DEPRECATION")

package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.impl.BukkitMaterial
import cn.fd.ratziel.module.item.impl.SimpleItemMaterial
import cn.fd.ratziel.module.item.impl.component.HideFlag
import org.bukkit.attribute.AttributeModifier
import taboolib.common.util.Strings
import taboolib.library.xseries.XEnchantment
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.BukkitAttribute
import taboolib.type.BukkitEquipment
import kotlin.jvm.optionals.getOrNull
import org.bukkit.enchantments.Enchantment as BukkitEnchantment

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
    fun matchEnchantment(source: String): BukkitEnchantment {
        val name = clean(source)
        return BukkitEnchantment.getByName(name) // BukkitEnchantment match
            ?: XEnchantment.matchXEnchantment(name).getOrNull()?.enchant // XEnchantment match
            ?: BukkitEnchantment.values().maxBy { Strings.similarDegree(it.name, name) } // Similar
    }

    /**
     * 匹配穿戴栏位
     */
    @JvmStatic
    fun matchEquipment(source: String): BukkitEquipment {
        val name = clean(source)
        return BukkitEquipment.fromString(name)
            ?: BukkitEquipment.entries.maxBy { Strings.similarDegree(it.name, name) }
    }

    /**
     * 匹配物品属性修饰符
     */
    @JvmStatic
    fun matchAttribute(source: String): BukkitAttribute {
        val name = clean(source)
        return BukkitAttribute.parse(name)
            ?: BukkitAttribute.entries.maxBy { Strings.similarDegree(it.name, name) }
    }

    /**
     * 匹配属性操作符
     */
    @JvmStatic
    fun matchAttributeOperation(source: String): AttributeModifier.Operation {
        val name = clean(source)
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
    fun matchHideFlag(source: String): HideFlag {
        val name = clean(source)
        return try {
            HideFlag.valueOf(name)
        } catch (_: IllegalArgumentException) {
            HideFlag.entries.maxBy { Strings.similarDegree(it.name, name) }
        }
    }

    /**
     * 匹配物品材料
     */
    @JvmStatic
    fun matchMaterial(source: String): ItemMaterial {
        val name = clean(source)
        // 尝试匹配准确名称
        val exactName: String? =
            BukkitMaterial.getMaterial(name)?.name  // BukkitMaterial Match
                ?: XMaterial.matchXMaterial(name).getOrNull()?.name // XMaterial Match
        return if (exactName != null) SimpleItemMaterial(exactName)
        else SimpleItemMaterial.materialsMap.maxBy { Strings.similarDegree(it.key, source) }.value // Similar
    }

    private fun clean(source: String): String = source.uppercase().replace(" ", "_").replace('-', '_')

}