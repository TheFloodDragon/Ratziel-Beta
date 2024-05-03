package cn.fd.ratziel.module.item.reflex

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.nbt.NBTCompound
import com.google.common.collect.Multimap
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.ItemMeta
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

    constructor() : this(new())

    constructor(tag: NBTCompound) : this(new(tag))

    /**
     * ItemMeta的CraftMetaItem处理对象
     */
    var handle: ItemMeta = when {
        obcClas.isAssignableFrom(raw::class.java) -> raw as ItemMeta // CraftMetaItem
        else -> throw UnsupportedTypeException(raw) // Unsupported Type
    }
        private set

    /**
     * 将 [ItemMeta] 应用到 [NBTCompound]
     */
    fun applyToTag(tag: NBTCompound) = tag.also {
        InternalImpl.applyToItem(handle, it.getData())
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
         * CraftMetaItem(DataComponentPatch tag)
         * @return CraftMetaItem
         */
        fun new(value: NBTCompound) = obcClass.invokeConstructor(InternalImpl.handleDataComponentPatch(value.getData()))

        /**
         * 创建空对象
         */
        fun new() = new(NBTCompound())

    }

    internal object InternalImpl {

        /**
         * CraftMetaItem#applyToItem(NBTTagCompound)
         * void applyToItem(Applicator itemTag)
         * @param craft CraftMetaItem
         * @param tag NBTTagCompound
         */
        fun applyToItem(craft: Any, tag: Any) {
            craft.invokeMethod<Void>("applyToItem", handleApplicator(tag))
        }

        /**
         * 1.20.5+: NBTTagCompound to CraftMetaItem.Applicator
         */
        fun handleApplicator(tag: Any) =
            if (MinecraftVersion.majorLegacy >= 12005)
                applicatorPutData(tag)
            else tag

        /**
         * 1.20.5+: NBTTagCompound to DataComponentPatch
         */
        fun handleDataComponentPatch(tag: Any) =
            if (MinecraftVersion.majorLegacy >= 12005)
                applicatorPutData(tag).invokeMethod<Any>("build")
            else tag

        /**
         * 处理1.20.5的CraftMetaItem.Applicator
         *
         * Applicator(){}
         * <T> Applicator put(ItemMetaKeyType<T> key, T value)
         * DataComponentPatch build()
         */
        fun applicatorPutData(tag: Any): Any {
            val applicator = applicatorClass.invokeConstructor()
            return applicator.invokeMethod<Any>("put", customDataKey, tag)!!
        }

        val applicatorClass by lazy {
            obcClass("inventory.CraftMetaItem\$Applicator")
        }

        val customDataKey by lazy {
            obcClass.getProperty<Any>("CUSTOM_DATA", isStatic = true)
        }

    }

}
