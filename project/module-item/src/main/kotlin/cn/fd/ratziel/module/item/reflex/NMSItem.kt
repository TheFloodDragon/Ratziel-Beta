@file:Suppress("unused")

package cn.fd.ratziel.module.item.reflex

import cn.fd.ratziel.module.item.nbt.NBTCompound
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.nbt.NBTTagCompound
import taboolib.library.reflex.Reflex.Companion.getProperty
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

    /**
     * 获取 [NMSItemStack]的 NBT (克隆)
     * @return [NBTCompound]
     */
    abstract fun getItemNBT(nmsItem: Any): NBTCompound?

    /**
     * 设置 [NMSItemStack]的 NBT (克隆)
     * @param nbt [NBTTagCompound]
     */
    abstract fun setItemNBT(nmsItem: Any, nbt: NBTCompound)

    /**
     * 克隆 [NMSItemStack]
     */
    abstract fun copyItem(nmsItem: Any): Any

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

    override fun getItemNBT(nmsItem: Any): NBTCompound? = nmsTagField.get(nmsItem)?.let { NBTCompound(it).clone() }

    override fun setItemNBT(nmsItem: Any, nbt: NBTCompound) = nmsTagField.set(nmsItem, nbt.clone().getData())

    override fun copyItem(nmsItem: Any): Any = nmsCloneMethod.invoke(nmsItem)!!

}

/**
 * 1.20.5+
 */
class NMSItemImpl2 : NMSItem() {

    override fun getItemNBT(nmsItem: Any): NBTCompound? {
        return NMSDataComponent.INSTANCE.save((nmsItem as NMSItemStack).componentsPatch)?.let { NBTCompound(it) }
    }

    override fun setItemNBT(nmsItem: Any, nbt: NBTCompound) {
        val dcp = NMSDataComponent.INSTANCE.parse(nbt.getData() as NBTTagCompound) as? DataComponentPatch
        val map = (nmsItem as NMSItemStack).getProperty<PatchedDataComponentMap>("components")
        map?.restorePatch(dcp)
    }

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
    }

}