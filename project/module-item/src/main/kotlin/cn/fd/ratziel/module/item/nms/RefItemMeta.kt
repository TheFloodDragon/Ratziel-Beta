package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.nbt.NBTCompound
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.ReflexClass
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
        obcClass.isAssignableFrom(raw::class.java) -> raw as ItemMeta // CraftMetaItem
        else -> throw UnsupportedTypeException(raw) // Unsupported Type
    }

    /**
     * 将 [ItemMeta] 应用到 [NBTCompound]
     */
    fun applyToTag(nbt: NBTCompound) = nbt.also { tag ->
        if (MinecraftVersion.majorLegacy >= 12005) {
            /*
            1.20.5+ 巨tm坑:
            if (this.customTag != null) {
              itemTag.put(CUSTOM_DATA, CustomData.a(this.customTag));
            }
             */
            val applicator = InternalUtil.applicatorConstructor.instance()!! // new Applicator
            InternalUtil.applyToItemMethod.invoke(handle, applicator) // Apply to the applicator
            val dcp = InternalUtil.applicatorToDcp(applicator) // Applicator to DataComponentPatch
            val newTag = NMS12005.INSTANCE.savePatch(dcp) // DataComponentPatch save to NBT
            if (newTag != null) tag.putAll(NBTCompound(newTag))
        } else {
            InternalUtil.applyToItemMethod.invoke(handle, tag.getData())
        }
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
        fun new(nbt: NBTCompound): Any {
            val handledTag =
                if (MinecraftVersion.majorLegacy >= 12005)
                    InternalUtil.nbtToDcp(nbt.getData())
                else nbt
            return obcClass.invokeConstructor(handledTag)
        }

        fun new() = new(NBTCompound())

    }

    internal object InternalUtil {

        /**
         * CraftMetaItem#applyToItem(NBTTagCompound)
         * void applyToItem(Applicator itemTag)
         */
        val applyToItemMethod by lazy {
            ReflexClass.of(obcClass).structure.methods.firstOrNull { it.name == "applyToItem" }
                ?: throw NoSuchMethodException("${obcClass.name}#applyToItem")
        }

        /**
         * 处理1.20.5的CraftMetaItem.Applicator
         *
         * Applicator(){}
         * <T> Applicator put(ItemMetaKeyType<T> key, T value)
         * DataComponentPatch build()
         */
        val applicatorClass by lazy {
            obcClass("inventory.CraftMetaItem\$Applicator")
        }

        /**
         * NBTTagCompound to DataComponentPatch
         */
        fun nbtToDcp(tag: Any): Any = applicatorToDcp(nbtToApplicator(tag))

        fun applicatorToDcp(applicator: Any): Any = applicatorBuildMethod.invoke(applicator)!!

        fun nbtToApplicator(nbt: Any): Any =
            applicatorPutMethod.invoke(
                applicatorConstructor.instance()!!,
                customDataKey,
                NMS12005.customDataConstructor.instance(nbt)
            )!!

        val customDataKey by lazy {
            obcClass.getProperty<Any>("CUSTOM_DATA", isStatic = true)
        }

        val applicatorConstructor by lazy {
            ReflexClass.of(applicatorClass).getConstructor()
        }

        val applicatorPutMethod by lazy {
            ReflexClass.of(applicatorClass).structure.methods.firstOrNull { it.name == "put" }
                ?: throw NoSuchMethodException("${applicatorClass.name}#put")
        }

        val applicatorBuildMethod by lazy {
            ReflexClass.of(applicatorClass).getMethod("build")
        }

    }

}