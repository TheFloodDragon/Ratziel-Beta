@file:Suppress("unused")

package cn.fd.ratziel.module.item.reflex

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.CustomData
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.nmsProxy
import net.minecraft.world.item.ItemStack as NMSItemStack

/**
 * NMSItem
 *
 * @author TheFloodDragon
 * @since 2024/4/30 19:32
 */
abstract class NMSItem {

    abstract fun getItemNBT(nmsItem: Any): Any?

    abstract fun setItemNBT(nmsItem: Any, nmsNBT: Any)

    abstract fun copyItem(nmsItem: Any): Any

    companion object {

        /**
         * nms.ItemStack
         *   1.17+ net.minecraft.world.item.ItemStack
         *   1.17- net.minecraft.server.$VERSION.ItemStack
         */
        val nmsClass by lazy { nmsClass("ItemStack") }

        val instance by lazy {
            if (MinecraftVersion.majorLegacy >= 12005) nmsProxy<NMSItem>("{name}Impl2") else NMSItemImpl1
        }

    }

}

/**
 * 1.20.4-
 */
object NMSItemImpl1 : NMSItem() {

    /**
     * private NBTTagCompound A
     * private NBTTagCompound tag
     */
    val nmsTagField by lazy {
        ReflexClass.of(nmsClass).structure.getField(
            if (MinecraftVersion.isUniversal) "A" else "tag"
        )
    }

    /**
     * public nms.ItemStack p()
     * public nms.ItemStack cloneItemStack()
     * public ItemStack s()
     */
    val nmsCloneMethod by lazy {
        ReflexClass.of(nmsClass).structure.getMethodByType(
            if (MinecraftVersion.majorLegacy >= 12005) "s"
            else if (MinecraftVersion.isUniversal) "p"
            else "cloneItemStack"
        )
    }

    override fun getItemNBT(nmsItem: Any): Any? = nmsTagField.get(nmsItem)

    override fun setItemNBT(nmsItem: Any, nmsNBT: Any) = nmsTagField.set(nmsItem, nmsNBT)

    override fun copyItem(nmsItem: Any): Any = nmsCloneMethod.invoke(nmsItem)!!

}

/**
 * 1.20.5+
 */
class NMSItemImpl2 : NMSItem() {

    val tagField = ReflexClass.of(CustomData::class.java).getField("tag", remap = true)

    override fun getItemNBT(nmsItem: Any): Any? {
        return tagField.get(getCustomData(nmsItem) ?: return null)
    }

    override fun setItemNBT(nmsItem: Any, nmsNBT: Any) {
        return tagField.set(getCustomData(nmsItem) ?: return, nmsNBT)
    }

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
    }

    fun applierHandle() {

    }

    fun getCustomData(nmsItem: Any): CustomData? = (nmsItem as NMSItemStack).get(DataComponents.CUSTOM_DATA)

}