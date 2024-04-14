@file:Suppress("DEPRECATION")

package cn.fd.ratziel.module.item.util

import cn.fd.ratziel.module.item.api.part.ItemMaterial
import cn.fd.ratziel.module.item.impl.part.VItemMaterial
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import taboolib.common.util.Strings
import taboolib.library.xseries.XEnchantment
import taboolib.module.nms.BukkitAttribute
import taboolib.type.BukkitEquipment
import kotlin.jvm.optionals.getOrElse
import org.bukkit.inventory.ItemFlag as HideFlag

/**
 * MetaMather
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:07
 */
object MetaMather {

    /**
     * 匹配物品魔咒
     */
    @JvmStatic
    fun matchEnchantment(source: String): Enchantment = Enchantment.getByName(source) ?: Enchantment.values().maxBy { Strings.similarDegree(it.name, source) }

    @JvmStatic
    fun matchXEnchantment(source: String): XEnchantment =
        XEnchantment.matchXEnchantment(source).getOrElse { XEnchantment.entries.maxBy { Strings.similarDegree(it.name, source) } }

    /**
     * 匹配穿戴栏位
     */
    @JvmStatic
    fun matchEquipment(source: String): BukkitEquipment =
        BukkitEquipment.fromString(source) ?: BukkitEquipment.entries.maxBy { Strings.similarDegree(it.name, source) }

    /**
     * 匹配物品属性修饰符
     */
    @JvmStatic
    fun matchAttribute(source: String): BukkitAttribute =
        BukkitAttribute.parse(source) ?: BukkitAttribute.entries.maxBy { Strings.similarDegree(it.name, source) }

    /**
     * 匹配属性操作符
     */
    @JvmStatic
    fun matchAttributeOperation(source: String): AttributeModifier.Operation =
        try {
            AttributeModifier.Operation.valueOf(source)
        } catch (_: IllegalArgumentException) {
            AttributeModifier.Operation.entries.maxBy { Strings.similarDegree(it.name, source) }
        }

    /**
     * 匹配物品(隐藏)标签
     */
    @JvmStatic
    fun matchHideFlag(source: String): HideFlag =
        try {
            HideFlag.valueOf(source)
        } catch (_: IllegalArgumentException) {
            HideFlag.entries.maxBy { Strings.similarDegree(it.name, source) }
        }

    /**
     * 匹配物品材料
     */
    @JvmStatic
    fun matchMaterial(source: String): ItemMaterial = VItemMaterial.materialsMap.maxBy { Strings.similarDegree(it.key, source) }.value

}