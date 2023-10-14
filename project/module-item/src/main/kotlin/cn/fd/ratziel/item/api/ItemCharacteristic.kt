package cn.fd.ratziel.item.api

import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag

/**
 * ItemCharacteristic - 物品特性
 *
 * 包括: 本地化名称、自定义模型数据(1.14+)、魔咒、标志、属性修饰符、无法破坏
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:21
 */
interface ItemCharacteristic {

    /**
     * 物品本地化名称
     */
    var localizedName: String?

    /**
     * 物品自定义模型数据 (1.14+)
     */
    var customModelData: Int?

    /**
     * 物品魔咒
     */
    var enchants: MutableMap<Enchantment, Int>

    /**
     * 物品标志
     */
    var itemFlags: MutableSet<ItemFlag>

    /**
     * 物品无法破坏属性
     */
    var unbreakable: Boolean

    /**
     * 物品属性修饰符
     */
    var attributeModifiers: MutableMap<Attribute, MutableList<AttributeModifier>>

}