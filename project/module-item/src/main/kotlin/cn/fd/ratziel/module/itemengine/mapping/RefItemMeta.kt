package cn.fd.ratziel.module.itemengine.mapping

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
    val clazz by lazy { obcClass("inventory.CraftMetaItem") }

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
     * Enchantments to NmsTag
     * CraftMetaItem#applyEnchantments(Map<Enchantment,Int>,NBTTagCompound,ItemMetaKey);Static
     */
    @JvmStatic
    fun applyEnchantments(nbtTag: Any, enchantments: Map<Enchantment, Int>) =
        clazz.invokeMethod<Void>(
            "applyEnchantments", enchantments, nbtTag,
            ItemMapping.ENCHANTMENTS.obcKey.obcData, isStatic = true
        )


    /**
     * AttributeModifiers to NmsTag
     * CraftMetaItem#applyModifiers(MultiMap<Attribute,AttributeModifier>,NBTTagCompound,ItemMetaKey);Static
     */
    @JvmStatic
    fun applyModifiers(nbtTag: Any, modifiers: Multimap<Attribute, AttributeModifier>) =
        clazz.invokeMethod<Void>(
            "applyModifiers", modifiers, nbtTag,
            ItemMapping.ATTRIBUTE_MODIFIERS.obcKey.obcData, isStatic = true
        )

    /**
     * NmsTag to AttributeModifiers
     * CraftMetaItem#buildModifiers(NBTTagCompound,ItemMetaKey):MultiMap<Attribute,AttributeModifier>;Static
     */
    @JvmStatic
    fun buildModifiers(nbtTag: Any): Multimap<Attribute, AttributeModifier> =
        clazz.invokeMethod<Multimap<Attribute, AttributeModifier>>(
            "buildModifiers", nbtTag,
            ItemMapping.ATTRIBUTE_MODIFIERS.obcKey.obcData, isStatic = true
        )!!

    /**
     * NmsTag to Enchantments
     * CraftMetaItem#buildEnchantments(NBTTagCompound,ItemMetaKey):Map<Enchantment,Integer>;Static
     */
    @JvmStatic
    fun buildEnchantments(nbtTag: Any): Multimap<Attribute, AttributeModifier> =
        clazz.invokeMethod<Multimap<Attribute, AttributeModifier>>(
            "buildEnchantments", nbtTag,
            ItemMapping.ENCHANTMENTS.obcKey.obcData, isStatic = true
        )!!

    /**
     * CraftMetaItem中的ItemMetaKey
     */
    internal class RefItemMetaKey(val fieldName: String) {

        val obcData = clazz.getProperty<Any?>(fieldName, isStatic = true)

        val NMS_NAME = obcData?.getProperty<String>("NBT")

        val BUKKIT_NAME = obcData?.getProperty<String>("Bukkit")

    }

}
