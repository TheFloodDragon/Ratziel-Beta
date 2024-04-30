package cn.fd.ratziel.module.item.reflex

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.reflex.RefItemStack.Companion.newObc
import com.google.common.collect.Multimap
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.obcClass

/**
 * RefItemMeta - CraftMetaItem映射类
 *
 * @author TheFloodDragon
 * @since 2023/10/22 10:14
 */
class RefItemMeta(raw: Any) {

    /**
     * ItemMeta的CraftMetaItem处理对象
     */
    private var handle: Any = when {
        obcClass::class.java.isAssignableFrom(raw::class.java) -> newObc(raw) // CraftMetaItem
        else -> throw UnsupportedTypeException(raw) // Unsupported Type
    }

    /**
     * CraftMetaItem.ItemMetaKey
     * 其他:
     * 1.20.4-: static final ItemMetaKey
     * 1.20.5+: static final ItemMetaKeyType<?>
     *     static final class ItemMetaKeyType<T> extends ItemMetaKey
     */
    class RefItemMetaKey(val fieldName: String, val targetClass: Class<*> = obcClass) {

        val obcData by lazy { targetClass.getProperty<Any>(fieldName, isStatic = true)!! }

        val nmsName by lazy { obcData.getProperty<String>("NBT")!! }

        val bukkitName by lazy { obcData.getProperty<String>("BUKKIT")!! }

    }

    companion object {

        /**
         * inventory.CraftMetaItem
         */
        val obcClass by lazy { obcClass("inventory.CraftMetaItem") }

        /**
         * CraftMetaItem#constructor(NBTTagCompound)
         * @return CraftMetaItem
         */
        fun new(value: Any) = obcClass.invokeConstructor(value)

        /**
         * 创建空对象
         */
        fun new() = new(NBTCompound.new())

    }

    //TODO(CNM)

    fun applyToTag(tag: NBTCompound) = tag.also {
        InternalImpl.applyToItem(handle, it.getData())
    }

    internal object InternalImpl {

        /**
         * CraftMetaItem#applyToItem(NBTTagCompound)
         * void applyToItem(Applicator itemTag)
         * @param craft CraftMetaItem
         * @param tag NBTTagCompound
         */
        fun applyToItem(craft: Any, tag: Any) {
            craft.invokeMethod<Void>("applyToItem", handle(tag))
        }

        internal val ENCHANTMENTS_KEY by lazy {
            obcClass.getProperty<Any>("ENCHANTMENTS", isStatic = true)
        }

        internal val ATTRIBUTES_KEY by lazy {
            obcClass.getProperty<Any>("ATTRIBUTES", isStatic = true)
        }

        /**
         * Enchantments to NBTTagCompound
         * CraftMetaItem#applyEnchantments(Map<Enchantment,Int>,NBTTagCompound,ItemMetaKey);Static
         */
        fun applyEnchantments(nbtTag: Any, enchantments: Map<Enchantment, Int>) =
            obcClass.invokeMethod<Void>(
                "applyEnchantments", enchantments, nbtTag,
                ENCHANTMENTS_KEY, isStatic = true
            )

        /**
         * NBTTagCompound to Enchantments
         * CraftMetaItem#buildEnchantments(NBTTagCompound,ItemMetaKey):Map<Enchantment,Integer>;Static
         */
        fun buildEnchantments(nbtTag: Any): Multimap<Attribute, AttributeModifier> =
            obcClass.invokeMethod<Multimap<Attribute, AttributeModifier>>(
                "buildEnchantments", nbtTag,
                ENCHANTMENTS_KEY, isStatic = true
            )!!


        /**
         * AttributeModifiers to NBTTagCompound
         * CraftMetaItem#applyModifiers(MultiMap<Attribute,AttributeModifier>,NBTTagCompound,ItemMetaKey);Static
         */
        fun applyModifiers(nbtTag: Any, modifiers: Multimap<Attribute, AttributeModifier>) =
            obcClass.invokeMethod<Void>(
                "applyModifiers", modifiers, nbtTag,
                ATTRIBUTES_KEY, isStatic = true
            )

        /**
         * NBTTagCompound to AttributeModifiers
         * CraftMetaItem#buildModifiers(NBTTagCompound,ItemMetaKey):MultiMap<Attribute,AttributeModifier>;Static
         */
        fun buildModifiers(nbtTag: Any): Multimap<Attribute, AttributeModifier> =
            obcClass.invokeMethod<Multimap<Attribute, AttributeModifier>>(
                if (MinecraftVersion.isHigherOrEqual(12005)) "buildModifiersLegacy" else "buildModifiers", nbtTag,
                ATTRIBUTES_KEY, isStatic = true
            )!!

        /**
         * 处理1.20.5的CraftMetaItem.Applicator
         *
         * Applicator(){}
         * <T> Applicator put(ItemMetaKeyType<T> key, T value)
         */
        fun handleApplicator(tag: Any): Any {
            val applicator = applicatorClass.invokeConstructor()
            return applicator.invokeMethod<Any>("put", customDataKey, tag)!!
        }

        fun handle(tag: Any) = if (MinecraftVersion.isHigherOrEqual(12005)) handleApplicator(tag) else tag

        val applicatorClass by lazy {
            obcClass("inventory.CraftMetaItem\$Applicator")
        }

        val customDataKey by lazy {
            obcClass.getProperty<Any>("CUSTOM_DATA", isStatic = true)
        }

    }

}