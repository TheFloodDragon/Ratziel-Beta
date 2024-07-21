package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.function.util.uncheck
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nbt.NMSUtil
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
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
     * [MetaType]
     */
    val metaType by lazy {
        MetaType.registry.find { it.clazz == handle::class.java } ?: MetaType(handle::class.java)
    }

    /**
     * 将 [ItemMeta] 应用到 [NBTCompound]
     */
    fun applyToTag(tag: NBTCompound) = tag.also {
        metaType.applyToItem(handle, tag)
    }

    companion object {

        /**
         * inventory.CraftMetaItem
         */
        val META_ITEM: MetaType<ItemMeta> = MetaType("inventory.CraftMetaItem")
        val META_SKULL: MetaType<SkullMeta> = MetaType("inventory.CraftMetaSkull")

        /**
         * CraftMetaItem#constructor(NBTTagCompound)
         * CraftMetaItem(DataComponentPatch tag)
         * @return CraftMetaItem
         */
        fun <T : ItemMeta> new(type: MetaType<T>, tag: NBTCompound): T = type.new(tag)

        @JvmStatic
        fun <T : ItemMeta> of(type: MetaType<T>) = of(type, NBTCompound())

        @JvmStatic
        fun <T : ItemMeta> of(type: MetaType<T>, tag: NBTCompound) = RefItemMeta(new(type, tag))

    }

    class MetaType<T : ItemMeta>(
        val clazz: Class<in T>,
    ) {

        init {
            registry.add(this)
        }

        companion object {
            val registry: MutableSet<MetaType<*>> = CopyOnWriteArraySet()
        }

        internal constructor(obcName: String) : this(uncheck<Class<in T>>(obcClass(obcName)))

        /**
         * CraftMetaItem#constructor(NBTTagCompound)
         * CraftMetaItem(DataComponentPatch tag)
         * @return CraftMetaItem
         */
        private val craftMetaConstructor by lazy {
            ReflexClass.of(clazz, false).structure.getConstructorByType(
                if (MinecraftVersion.majorLegacy >= 12005) NMS12005.DATA_COMPONENT_PATCH_CLASS else NMSUtil.NtCompound.nmsClass
            )
        }

        private val applyToItemMethod by lazy {
            ReflexClass.of(clazz, false).structure.getMethodByType(
                "applyToItem",
                if (MinecraftVersion.majorLegacy >= 12005) InternalUtil.applicatorClass else NMSUtil.NtCompound.nmsClass
            )
        }

        internal fun new(tag: NBTCompound): T {
            val handled =
                if (MinecraftVersion.majorLegacy >= 12005)
                    NMS12005.INSTANCE.parsePatch(tag.getData())!!
                else tag
            return uncheck(craftMetaConstructor.instance(handled)!!)
        }

        internal fun applyToItem(meta: ItemMeta, tag: NBTCompound) {
            if (MinecraftVersion.majorLegacy >= 12005) {
                /*
                1.20.5+ 巨tm坑:
                if (this.customTag != null) {
                  itemTag.put(CUSTOM_DATA, CustomData.a(this.customTag));
                }
                 */
                val applicator = InternalUtil.applicatorConstructor.instance()!! // new Applicator
                applyToItemMethod.invoke(meta, applicator) // Apply to the applicator
                val dcp = InternalUtil.applicatorToDcp(applicator) // Applicator to DataComponentPatch
                val newTag = NMS12005.INSTANCE.savePatch(dcp) // DataComponentPatch save to NBT
                if (newTag != null) tag.mergeShallow(NBTCompound(newTag), true)
            } else {
                applyToItemMethod.invoke(meta, tag.getData())
            }
        }

    }

    internal object InternalUtil {

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

        fun applicatorToDcp(applicator: Any): Any = applicatorBuildMethod.invoke(applicator)!!

        val applicatorConstructor by lazy {
            ReflexClass.of(applicatorClass, false).getConstructor()
        }

        val applicatorBuildMethod by lazy {
            ReflexClass.of(applicatorClass, false).getMethod("build")
        }

    }

}
