package cn.fd.ratziel.module.item.reflex

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.nbt.NBTCompound
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
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
        obcClass.isAssignableFrom(raw::class.java) -> raw as ItemMeta // CraftMetaItem
        else -> throw UnsupportedTypeException(raw) // Unsupported Type
    }

    /**
     * 将 [ItemMeta] 应用到 [NBTCompound]
     */
    fun applyToTag(tag: NBTCompound) = tag.also {
        if (MinecraftVersion.majorLegacy >= 12005) {
            /*
            1.20.5+ 巨tm坑:
            if (this.customTag != null) {
              itemTag.put(CUSTOM_DATA, CustomData.a(this.customTag));
            }
             */
            val newTag = InternalUtil.applicatorClass.invokeConstructor()
            InternalUtil.invokeApplyToItem(handle, newTag)
            val dcp = InternalUtil.applicatorToDCP(newTag)
            val nbtTag = NMSItem.INSTANCE.getNBTFromDCP(dcp)
            if (nbtTag != null) tag.merge(NBTCompound(nbtTag), true)
        } else {
            InternalUtil.invokeApplyToItem(handle, tag.getData())
        }
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
        fun new(tag: NBTCompound): Any {
            val handledTag = if (MinecraftVersion.majorLegacy >= 12005) InternalUtil.toDCP(tag.getData()) else tag
            return obcClass.invokeConstructor(handledTag)
        }

        fun new() = new(NBTCompound())

    }

    internal object InternalUtil {

        /**
         * Only 1.20.5+
         */
        val customDataClass by lazy {
            nmsClass("CustomData")
        }

        /**
         * private CustomData(NBTTagCompound var0)
         */
        fun newCustomData(tag: Any) = customDataClass.invokeConstructor(tag)

        /**
         * Only 1.20.5+
         */
        val applicatorClass by lazy {
            obcClass("inventory.CraftMetaItem\$Applicator")
        }

        /**
         * CraftMetaItem#applyToItem(NBTTagCompound)
         * void applyToItem(Applicator itemTag)
         */
        fun invokeApplyToItem(handle: Any, tag: Any) {
            handle.invokeMethod<Any>("applyToItem", tag)
        }

        /**
         * 1.20.5+: NBTTagCompound to DataComponentPatch
         */
        fun toDCP(tag: Any): Any = applicatorToDCP(toApplicator(tag))

        fun applicatorToDCP(applicator: Any): Any = applicator.invokeMethod<Any>("build")!!

        /**
         * 处理1.20.5的CraftMetaItem.Applicator
         *
         * Applicator(){}
         * <T> Applicator put(ItemMetaKeyType<T> key, T value)
         * DataComponentPatch build()
         */
        fun toApplicator(tag: Any): Any =
            applicatorClass.invokeConstructor()
                .invokeMethod<Any>("put", customDataKey, newCustomData(tag))!!

        val customDataKey by lazy {
            obcClass.getProperty<Any>("CUSTOM_DATA", isStatic = true)
        }

    }

}
