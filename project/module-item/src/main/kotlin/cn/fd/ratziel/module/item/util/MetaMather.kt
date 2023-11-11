package cn.fd.ratziel.module.item.util

import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemFlag
import taboolib.common.util.Strings
import taboolib.library.xseries.XEnchantment
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.BukkitAttribute
import taboolib.type.BukkitEquipment
import kotlin.jvm.optionals.getOrElse

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
    fun matchEnchantment(source: String) =
        XEnchantment.matchXEnchantment(source).getOrElse {
            XEnchantment.entries.maxBy { Strings.similarDegree(it.name, source) }
        }.enchant!!

    /**
     * 匹配穿戴栏位
     */
    @JvmStatic
    fun matchEquipment(source: String) =
        (BukkitEquipment.fromString(source) ?: BukkitEquipment.entries.maxByOrNull {
            Strings.similarDegree(it.name, source)
        })?.bukkit

    /**
     * 匹配物品属性修饰符
     */
    @JvmStatic
    fun matchAttribute(source: String) =
        (BukkitAttribute.parse(source) ?: BukkitAttribute.entries.maxBy {
            Strings.similarDegree(it.name, source)
        }).toBukkit()

    /**
     * 匹配属性操作符
     */
    @JvmStatic
    fun matchAttributeOperation(source: String) =
        try {
            AttributeModifier.Operation.valueOf(source)
        } catch (_: IllegalArgumentException) {
            null
        }
            ?: AttributeModifier.Operation.entries.maxBy {
                Strings.similarDegree(it.name, source)
            }

    /**
     * 匹配物品(隐藏)标签
     */
    @JvmStatic
    fun matchItemFlag(source: String) =
        try {
            ItemFlag.valueOf(source)
        } catch (_: IllegalArgumentException) {
            null
        }
            ?: ItemFlag.entries.maxBy {
                Strings.similarDegree(it.name, source)
            }

    /**
     * 匹配物品材质
     */
    @JvmStatic
    fun matchMaterial(source: String) =
        XMaterial.matchXMaterial(source).getOrElse {
            XMaterial.entries.maxBy { Strings.similarDegree(it.name, source) }
        }

}