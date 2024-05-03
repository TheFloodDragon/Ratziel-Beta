package cn.fd.ratziel.module.item.api.part

import cn.fd.ratziel.module.item.api.ItemComponent
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier

/**
 * ItemSundry - 物品杂项
 *
 * @author TheFloodDragon
 * @since 2024/5/3 20:59
 */
interface ItemSundry : ItemComponent {

    /**
     * 物品自定义模型数据 (1.14+)
     */
    var customModelData: Int?

    /**
     * 物品隐藏标签 (1.20.5- 但仍能使用)
     */
    var hideFlags: MutableSet<HideFlag>?

    /**
     * 物品属性修饰符
     */
    var bukkitAttributes: MutableMap<Attribute, MutableList<AttributeModifier>>?

}

typealias HideFlag = org.bukkit.inventory.ItemFlag