package cn.fd.ratziel.module.itemengine.ref

import cn.fd.ratziel.module.itemengine.nbt.NBTCompound
import com.google.common.collect.Multimap
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.obcClass

/**
 * RefItemMeta - CraftMetaItem映射类
 *
 * @author TheFloodDragon
 * @since 2023/10/22 10:14
 */
object RefItemMeta {

    @JvmStatic
    val clazz by lazy {
        obcClass("inventory.CraftMetaItem")
    }

    /**
     * CraftMetaItem#constructor(NBTTagCompound)
     * @return CraftMetaItem
     */
    @JvmStatic
    fun new(value: Any) = clazz.invokeConstructor(value)

    /**
     * 创建空对象
     */
    @JvmStatic
    fun new() = new(NBTCompound.new())

    /**
     * CraftMetaItem#applyToItem(NBTTagCompound)
     * @param craft CraftMetaItem
     * @param nbtTag NBTTagCompound
     */
    @JvmStatic
    fun applyToItem(craft: Any, nbtTag: Any) {
        craft.invokeMethod<Void>("applyToItem", nbtTag)
    }


    /**
     * CraftMetaItem#applyEnchantments(Map<Enchantment,Int>,NBTTagCompound,ItemMetaKey);Static
     * @param nbtTag NBTTagCompound
     * CraftMetaItem#ENCHANTMENTS:ItemMetaKey;Static
     */
    @JvmStatic
    fun applyEnchantments(nbtTag: Any, enchantments: Map<Enchantment, Int>) {
        val key = clazz.getProperty<Any>("ENCHANTMENTS", isStatic = true)
        clazz.invokeMethod<Void>("applyEnchantments", enchantments, nbtTag, key, isStatic = true)
    }

    /**
     * CraftMetaItem#applyModifiers(Multimap<Attribute,AttributeModifier>,NBTTagCompound,ItemMetaKey);Static
     * @param nbtTag NBTTagCompound
     * CraftMetaItem#ATTRIBUTES:ItemMetaKey;Static
     */
    @JvmStatic
    fun applyModifiers(nbtTag: Any, modifiers: Multimap<Attribute, AttributeModifier>) {
        val key = clazz.getProperty<Any>("ATTRIBUTES", isStatic = true)
        clazz.invokeMethod<Void>("applyModifiers", modifiers, nbtTag, key, isStatic = true)
    }

}