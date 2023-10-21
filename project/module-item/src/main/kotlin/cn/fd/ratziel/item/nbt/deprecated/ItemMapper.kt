package cn.fd.ratziel.item.nbt.deprecated

import taboolib.library.reflex.ClassAnalyser
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.obcClass

/**
 * ItemMapper
 *
 * @author TheFloodDragon
 * @since 2023/10/21 20:19
 */
@Deprecated("可能会取消的设计")
sealed class ItemMapper {

    abstract class Ref(protected val obj: Any) {
        abstract fun get(): Any?
    }

    /**
     * NBTTagCompound
     *   1.17+ net.minecraft.nbt.NBTTagCompound
     *   1.12- net.minecraft.server.v1_12_R1.NBTTagCompound
     */
    class RefNBTTagCompound(obj: Any) : Ref(obj) {
        companion object {
            val nmsClass by lazy {
                if (MinecraftVersion.isLowerOrEqual(MinecraftVersion.V1_12))
                    nmsClass("NBTTagCompound")
                else Class.forName("net.minecraft.nbt.NBTTagCompound")
            }
        }

        override fun get() = obj

    }

    sealed class RefCraftItemMeta {

        companion object {
            val obcClass by lazy {
                obcClass("inventory.CraftMetaItem")
            }
            private val analyse by lazy { ClassAnalyser.analyse(obcClass) }
        }

        private constructor()

        private lateinit var obj: Any

        /**
         * CraftMetaItem#constructor(itemTag)
         * @param tag RefNBTTagCompound
         */
        constructor(itemTag: RefNBTTagCompound) : this() {
            obj = analyse.getConstructorByType(RefNBTTagCompound.nmsClass).instance(itemTag.get())!!
        }

        /**
         * CraftMetaItem#applyToItem(itemTag)
         * @param itemTag RefNBTTagCompound
         */
        fun applyToItem(itemTag: RefNBTTagCompound) {
            obj.invokeMethod<Void>("applyToItem", itemTag.get())
        }

    }

}