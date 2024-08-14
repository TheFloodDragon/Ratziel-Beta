package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.function.uncheck
import cn.fd.ratziel.module.nbt.NBTCompound
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.obcClass
import java.util.concurrent.CopyOnWriteArraySet

/**
 * RefItemMeta
 *
 * @author TheFloodDragon
 * @since 2023/10/22 10:14
 */
class RefItemMeta<T : ItemMeta>(raw: T) {

    /**
     * ItemMeta子类的CraftMetaItem处理对象
     */
    var handle: T = raw as? T ?: throw UnsupportedTypeException(raw)

    /**
     * [CraftMetaType]
     */
    val metaType by lazy {
        registry.find { it.craftClass.isAssignableFrom(handle::class.java) } ?: CraftMetaType(handle::class.java)
    }

    /**
     * 将 [ItemMeta] 应用到 [NBTCompound]
     */
    fun applyToTag(tag: NBTCompound) = tag.also {
        metaType.applyToItem(handle, tag)
    }

    companion object {

        /**
         * Registry for [CraftMetaType]
         */
        val registry: MutableSet<CraftMetaType<*>> = CopyOnWriteArraySet()

        /**
         * inventory.CraftMetaItem
         */
        val META_ITEM: CraftMetaType<ItemMeta> = CraftMetaType("inventory.CraftMetaItem")
        val META_SKULL: CraftMetaType<SkullMeta> = CraftMetaType("inventory.CraftMetaSkull")

        /**
         * CraftMetaItem#constructor(NBTTagCompound)
         * CraftMetaItem(DataComponentPatch tag)
         * @return CraftMetaItem
         */
        fun <T : ItemMeta> new(type: CraftMetaType<T>, tag: NBTCompound): T = type.new(tag)

        @JvmStatic
        fun <T : ItemMeta> of(type: CraftMetaType<T>) = of(type, NBTCompound())

        @JvmStatic
        fun <T : ItemMeta> of(type: CraftMetaType<T>, tag: NBTCompound) = RefItemMeta(new(type, tag))

    }

    class CraftMetaType<T : ItemMeta>(
        val craftClass: Class<T>,
    ) {

        init {
            registry.add(this)
        }

        internal constructor(obcName: String) : this(uncheck<Class<T>>(obcClass(obcName)))

        /**
         * CraftMetaItem#constructor(NBTTagCompound)
         * CraftMetaItem(DataComponentPatch tag)
         * @return CraftMetaItem
         */
        private val craftMetaConstructor by lazy {
            ReflexClass.of(craftClass, false).structure.getConstructorByType(
                if (MinecraftVersion.majorLegacy >= 12005) NMS12005.dataComponentPatchClass else nbtTagCompoundClass
            )
        }

        private val applyToItemMethod by lazy {
            ReflexClass.of(craftClass, false).structure.getMethodByType(
                "applyToItem",
                if (MinecraftVersion.majorLegacy >= 12005) applicatorClass else nbtTagCompoundClass
            )
        }

        internal fun new(tag: NBTCompound): T {
            val handled = if (MinecraftVersion.majorLegacy >= 12005) NMS12005.INSTANCE.parsePatch(tag)!! else NMSItem.INSTANCE.toNms(tag)
            return uncheck(craftMetaConstructor.instance(handled)!!)
        }

        internal fun applyToItem(meta: ItemMeta, tag: NBTCompound) {
            if (MinecraftVersion.majorLegacy >= 12005) {/*
                1.20.5+ 巨tm坑:
                if (this.customTag != null) {
                  itemTag.put(CUSTOM_DATA, CustomData.a(this.customTag));
                }
                 */
                val applicator = applicatorConstructor.instance()!! // new Applicator
                applyToItemMethod.invoke(meta, applicator) // Apply to the applicator
                val dcp = applicatorBuildMethod.invoke(applicator)!! // Applicator to DataComponentPatch
                val newTag = NMS12005.INSTANCE.savePatch(dcp) // DataComponentPatch save to NBT
                if (newTag != null) tag.mergeShallow(newTag, true)
            } else {
                applyToItemMethod.invoke(meta, NMSItem.INSTANCE.toNms(tag))
            }
        }

        companion object {

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

            val applicatorConstructor by lazy {
                ReflexClass.of(applicatorClass, false).getConstructor()
            }

            val applicatorBuildMethod by lazy {
                ReflexClass.of(applicatorClass, false).getMethod("build")
            }

            val nbtTagCompoundClass by lazy {
                nmsClass("NBTTagCompound")
            }

        }

    }

}
