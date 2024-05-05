@file:Suppress("unused")

package cn.fd.ratziel.module.item.reflex

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.item.component.CustomData
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.nmsProxy
import kotlin.jvm.optionals.getOrNull
import net.minecraft.world.item.ItemStack as NMSItemStack

/**
 * NMSItem
 *
 * @author TheFloodDragon
 * @since 2024/4/30 19:32
 */
abstract class NMSItem {

    /**
     * 获取 [NMSItemStack]的 NBT
     * @return [NBTTagCompound]
     */
    abstract fun getItemNBT(nmsItem: Any): Any?

    /**
     * 设置 [NMSItemStack]的 NBT
     * @param nmsNBT [NBTTagCompound]
     */
    abstract fun setItemNBT(nmsItem: Any, nmsNBT: Any)

    /**
     * 克隆 [NMSItemStack]
     */
    abstract fun copyItem(nmsItem: Any): Any

    /**
     * 1.20.5 从 [DataComponentPatch] 获取 [NBTTagCompound]
     */
    @Deprecated("草你妈")
    open fun getNBTFromDCP(dcp: Any): Any? = throw NotImplementedError("Not implement. It's unsupported below 1.20.5!")

    companion object {

        /**
         * nms.ItemStack
         *   1.17+ net.minecraft.world.item.ItemStack
         *   1.17- net.minecraft.server.$VERSION.ItemStack
         */
        val nmsClass by lazy { nmsClass("ItemStack") }

        val INSTANCE by lazy {
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
@Suppress("DEPRECATION")
class NMSItemImpl2 : NMSItem() {

    override fun getItemNBT(nmsItem: Any): Any? {
        val customData = (nmsItem as NMSItemStack).get(DataComponents.CUSTOM_DATA)
        return nbtFromCustomData(customData)
    }

    override fun setItemNBT(nmsItem: Any, nmsNBT: Any) {
        val customData = RefItemMeta.InternalUtil.newCustomData(nmsNBT as NBTTagCompound) as CustomData
        (nmsItem as NMSItemStack).set(DataComponents.CUSTOM_DATA, customData)
    }

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
    }

    override fun getNBTFromDCP(dcp: Any): Any? = nbtFromCustomData((dcp as DataComponentPatch).get(DataComponents.CUSTOM_DATA)?.getOrNull())

    private fun nbtFromCustomData(customData: CustomData?) =
        try {
            customData?.unsafe
        } catch (ex: Exception) {
            customData?.invokeMethod("tag", remap = true)
        }

}