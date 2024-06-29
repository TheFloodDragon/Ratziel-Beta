package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.nbt.NBTCompound
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.invokeMethod
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

    constructor() : this(metaClass)

    constructor(clazz: Class<*>) : this(clazz, NBTCompound())

    constructor(clazz: Class<*>, tag: NBTCompound) : this(new(clazz, tag))

    /**
     * ItemMeta子类的CraftMetaItem处理对象
     */
    var handle: ItemMeta = raw as? ItemMeta ?: throw UnsupportedTypeException(raw)

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
            handle.invokeMethod<Any>("applyToItem", applicator) // Apply to the applicator
            val dcp = InternalUtil.applicatorToDcp(applicator) // Applicator to DataComponentPatch
            val newTag = NMS12005.INSTANCE.savePatch(dcp) // DataComponentPatch save to NBT
            if (newTag != null) tag.merge(NBTCompound(newTag), true)
        } else {
            handle.invokeMethod<Any>("applyToItem", tag.getData())
        }
    }

    companion object {

        /**
         * inventory.CraftMetaItem
         */
        val metaClass by lazy { obcClass("inventory.CraftMetaItem") }

        val skullClass by lazy { obcClass("inventory.CraftMetaSkull") }

        /**
         * CraftMetaItem#constructor(NBTTagCompound)
         * CraftMetaItem(DataComponentPatch tag)
         * @return CraftMetaItem
         */
        fun new(clazz: Class<*>, nbt: NBTCompound): Any {
            val handledTag =
                if (MinecraftVersion.majorLegacy >= 12005)
                    InternalUtil.nbtToDcp(nbt.getData())
                else nbt
            return clazz.invokeConstructor(handledTag)
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
            metaClass.getProperty<Any>("CUSTOM_DATA", isStatic = true)
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
