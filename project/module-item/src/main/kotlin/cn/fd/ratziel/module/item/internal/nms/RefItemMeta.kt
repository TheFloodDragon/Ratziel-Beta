package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.nbt.NBTHelper
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.reflex.ClassConstructor
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
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
    var handle: T = raw

    /**
     * [CraftMetaType]
     */
    val metaType by lazy {
        registry.find { it.craftClass.isAssignableFrom(handle::class.java) } ?: CraftMetaType(handle::class.java)
    }

    /**
     * 将 [ItemMeta] 应用到 [NbtCompound]
     */
    fun applyToTag(sourceTag: NbtCompound = NbtCompound()): NbtCompound = metaType.applyToItem(handle, sourceTag)

    companion object {

        /**
         * Registry for [CraftMetaType]
         */
        val registry: MutableSet<CraftMetaType<*>> = CopyOnWriteArraySet()

        /**
         * inventory.CraftMetaItem
         */
        val META_ITEM = CraftMetaType<ItemMeta>("inventory.CraftMetaItem")
        val META_SKULL = CraftMetaType<SkullMeta>("inventory.CraftMetaSkull")

        /**
         * CraftMetaItem#constructor(NBTTagCompound)
         * CraftMetaItem(DataComponentPatch tag)
         * @return CraftMetaItem
         */
        fun <T : ItemMeta> new(type: CraftMetaType<T>, tag: NbtCompound): T = type.new(tag)

        @JvmStatic
        fun <T : ItemMeta> of(type: CraftMetaType<T>) = of(type, NbtCompound())

        @JvmStatic
        fun <T : ItemMeta> of(type: CraftMetaType<T>, tag: NbtCompound) = RefItemMeta(new(type, tag))

    }

    class CraftMetaType<T : ItemMeta>(
        val craftClass: Class<T>,
    ) {

        init {
            registry.add(this)
        }

        @Suppress("UNCHECKED_CAST")
        internal constructor(obcName: String) : this(obcClass(obcName) as Class<T>)

        /**
         * CraftMetaItem#constructor(NBTTagCompound)
         * CraftMetaItem(DataComponentPatch tag)
         * CraftMetaItem(DataComponentPatch tag, Set<DataComponentType<?>>) - Fuck you, Paper!
         * @return CraftMetaItem
         */
        @Suppress("UNCHECKED_CAST")
        private val constructor: Pair<ClassConstructor, (NbtCompound) -> T> by lazy {
            val structure = ReflexClass.of(craftClass, false).structure
            if (MinecraftVersion.versionId >= 12005) {
                val common = structure.getConstructorByTypeSilently(NMS12005.DATA_COMPONENT_PATCH_CLASS)
                if (common == null) {
                    val paper = structure.getConstructorByType(NMS12005.DATA_COMPONENT_PATCH_CLASS, Set::class.java)
                    paper to { paper.instance(NMS12005.INSTANCE.parsePatch(it)!!, mutableSetOf<Any>())!! as T }
                } else common to { common.instance(NMS12005.INSTANCE.parsePatch(it)!!)!! as T }
            } else {
                val legacy = structure.getConstructorByType(nbtTagCompoundClass)
                legacy to { legacy.instance(NBTHelper.toNms(it))!! as T }
            }
        }

        private val applyToItemMethod by lazy {
            ReflexClass.of(craftClass, false).structure.getMethodByType(
                "applyToItem",
                if (MinecraftVersion.versionId >= 12005) applicatorClass else nbtTagCompoundClass
            )
        }

        internal fun new(sourceTag: NbtCompound): T {
            return constructor.second.invoke(sourceTag)
        }

        internal fun applyToItem(meta: ItemMeta, sourceTag: NbtCompound): NbtCompound {
            if (MinecraftVersion.versionId >= 12005) {
                // 使用Paper的方法 (**Paper天天搁那改)
                if (paperApplyMetaToItemMethod != null) {
                    val nmsItem = RefItemStack.newNms()
                    paperApplyMetaToItemMethod!!.invokeStatic(nmsItem, meta)
                    val newTag = NMSItem.INSTANCE.getTag(nmsItem)
                    if (newTag != null) sourceTag.mergeShallow(newTag, true)
                } else {
                    val applicator = applicatorConstructor.instance()!! // new Applicator
                    applyToItemMethod.invoke(meta, applicator) // Apply to the applicator
                    val dcp = applicatorBuildMethod.invoke(applicator)!! // Applicator to DataComponentPatch
                    val newTag = NMS12005.INSTANCE.savePatch(dcp) // DataComponentPatch save to NBT
                    if (newTag != null) sourceTag.mergeShallow(newTag, true)
                }
                return sourceTag
            } else {
                val nmsTag = NBTHelper.toNms(sourceTag)
                applyToItemMethod.invoke(meta, nmsTag)
                return NBTHelper.fromNms(nmsTag) as NbtCompound
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

            /**
             * 无语了已经, 我要和Paper爆了
             * public static void applyMetaToItem(net.minecraft.world.item.ItemStack itemStack, ItemMeta itemMeta)
             */
            val paperApplyMetaToItemMethod by lazy {
                ReflexClass.of(RefItemStack.obcClass, false).structure
                    .getMethodByTypeSilently("applyMetaToItem", RefItemStack.nmsClass, ItemMeta::class.java)
            }

        }

    }

}
