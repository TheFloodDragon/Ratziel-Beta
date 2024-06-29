package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.module.item.nbt.NBTCompound
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.nbt.NBTTagCompound
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy
import java.util.concurrent.ConcurrentHashMap
import net.minecraft.world.item.Item as NMSItemType
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
    abstract fun getTag(nmsItem: Any): NBTCompound?

    /**
     * 设置 [NMSItemStack]的 NBT (克隆)
     * @param tag [NBTTagCompound]
     */
    abstract fun setTag(nmsItem: Any, tag: NBTCompound)

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

    override fun getTag(nmsItem: Any): NBTCompound? =
        RefItemStack.InternalUtil.nmsTagField.get(nmsItem)?.let { NBTCompound(it).clone() }

    override fun setTag(nmsItem: Any, tag: NBTCompound) =
        RefItemStack.InternalUtil.nmsTagField.set(nmsItem, tag.clone().getData())

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

    override fun getTag(nmsItem: Any): NBTCompound? {
        val dcp = (nmsItem as NMSItemStack).componentsPatch
        return NMS12005.INSTANCE.savePatch(dcp)?.let { NBTCompound(it) }
    }

    override fun setTag(nmsItem: Any, tag: NBTCompound) {
        val dcp = NMS12005.INSTANCE.parsePatch(tag.getData() as NBTTagCompound) as? DataComponentPatch
        val components = componentsField.get(nmsItem) as? PatchedDataComponentMap
        if (components != null) {
            components.restorePatch(dcp)
        } else {
            val newComponents = PatchedDataComponentMap(DataComponentMap.EMPTY)
            newComponents.restorePatch(dcp)
            componentsField.set(nmsItem, newComponents)
        }
    }

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
    }

}