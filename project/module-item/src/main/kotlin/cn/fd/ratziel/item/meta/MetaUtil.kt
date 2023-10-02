package cn.fd.ratziel.item.meta

import cn.fd.ratziel.adventure.serializeByMiniMessage
import cn.fd.ratziel.adventure.toJsonFormat
import net.kyori.adventure.text.Component
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.util.Strings
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.xseries.XEnchantment
import taboolib.module.nms.BukkitAttribute
import taboolib.module.nms.MinecraftVersion
import taboolib.type.BukkitEquipment
import kotlin.jvm.optionals.getOrElse

fun ItemMeta.setDisplayName(component: Component) = this.apply {
    if (MinecraftVersion.isLower(MinecraftVersion.V1_17)) {
        setDisplayName(serializeByMiniMessage(component))
    } else setProperty("displayName", component.toJsonFormat())
}

fun ItemMeta.setLore(components: Iterable<Component>) = this.apply {
    if (MinecraftVersion.isLower(MinecraftVersion.V1_17)) {
        lore = components.map { serializeByMiniMessage(it) }
    } else setProperty("lore", components.map { it.toJsonFormat() })
}

/**
 * 匹配物品魔咒
 */
fun matchEnchantment(source: String) =
    XEnchantment.matchXEnchantment(source).getOrElse {
        XEnchantment.entries.maxBy { Strings.similarDegree(it.name, source) }
    }.enchant!!

/**
 * 匹配穿戴栏位
 */
fun matchEquipment(source: String) =
    (BukkitEquipment.fromString(source) ?: BukkitEquipment.entries.maxBy {
        Strings.similarDegree(it.name, source)
    }).bukkit

/**
 * 匹配物品属性修饰符
 */
fun matchAttribute(source: String) =
    (BukkitAttribute.parse(source) ?: BukkitAttribute.entries.maxBy {
        Strings.similarDegree(it.name, source)
    }).toBukkit()

/**
 * 匹配属性操作符
 */
fun matchAttributeOperation(source: String) =
    try {
        AttributeModifier.Operation.valueOf(source)
    } catch (_: IllegalArgumentException) { null }
        ?: AttributeModifier.Operation.entries.maxBy {
        Strings.similarDegree(it.name, source)
    }