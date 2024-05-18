package cn.fd.ratziel.module.item.reflex

import cn.fd.ratziel.module.item.nbt.NBTCompound
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.nbt.NBTTagCompound
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
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

        val INSTANCE by lazy {
            if (MinecraftVersion.majorLegacy >= 12005) nmsProxy<NMSItem>("{name}Impl2") else NMSItemImpl1
        }

    }

}

/**
 * 1.20.4-
 */
object NMSItemImpl1 : NMSItem() {

    override fun getItemNBT(nmsItem: Any): NBTCompound? =
        RefItemStack.InternalUtil.nmsTagField.get(nmsItem)?.let { NBTCompound(it).clone() }

    override fun setItemNBT(nmsItem: Any, nbt: NBTCompound) =
        RefItemStack.InternalUtil.nmsTagField.set(nmsItem, nbt.clone().getData())

    override fun copyItem(nmsItem: Any): Any =
        RefItemStack.InternalUtil.nmsCloneMethod.invoke(nmsItem)!!

}

/**
 * 1.20.5+
 */
@Suppress("unused")
class NMSItemImpl2 : NMSItem() {

    val componentsField by lazy {
        ReflexClass.of(RefItemStack.nmsClass).getField("components", remap = true)
    }

    override fun getItemNBT(nmsItem: Any): NBTCompound? {
        val dcp = (nmsItem as NMSItemStack).componentsPatch
        return NMS12005.INSTANCE.save(dcp)?.let { NBTCompound(it) }
    }

    override fun setItemNBT(nmsItem: Any, nbt: NBTCompound) {
        val dcp = NMS12005.INSTANCE.parse(nbt.getData() as NBTTagCompound) as? DataComponentPatch
        val components = componentsField.get(nmsItem) as? PatchedDataComponentMap
        components?.restorePatch(dcp)
    }

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
    }

}