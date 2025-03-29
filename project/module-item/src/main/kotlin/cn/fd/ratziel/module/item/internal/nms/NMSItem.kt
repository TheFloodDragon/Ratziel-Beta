package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.nbt.NbtHelper
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.PatchedDataComponentMap
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
     * 获取 [NMSItemStack] 的 NBT (克隆)
     * @return [NbtCompound]
     */
    abstract fun getTag(nmsItem: Any): NbtCompound?

    /**
     * 设置 [NMSItemStack] 的 NBT (克隆)
     */
    abstract fun setTag(nmsItem: Any, tag: NbtCompound)

    /**
     * 克隆 [NMSItemStack]
     */
    abstract fun copyItem(nmsItem: Any): Any

    /**
     * 合并内部标签数据 (浅合并)
     */
    abstract fun applyComponents(source: Any, target: Any)

    companion object {

        val INSTANCE by lazy {
            if (MinecraftVersion.versionId >= 12005)
                nmsProxy<NMSItem>("{name}Impl2")
            else nmsProxy<NMSItem>("{name}Impl1")
        }

    }

}

/**
 * 1.20.5+
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMSItemImpl2 : NMSItem() {

    val componentsField by lazy {
        ReflexClass.of(NMSItemStack::class.java).getField("components", remap = true)
    }

    override fun getTag(nmsItem: Any): NbtCompound? {
        val components = (componentsField.get(nmsItem) ?: return null) as PatchedDataComponentMap
        return NMS12005.INSTANCE.savePatch(components.asPatch())
    }

    override fun setTag(nmsItem: Any, tag: NbtCompound) {
        val components = (componentsField.get(nmsItem) ?: return) as PatchedDataComponentMap
        val dcp = NMS12005.INSTANCE.parsePatch(tag) as DataComponentPatch
        components.restorePatch(dcp)
    }

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
    }

    override fun applyComponents(source: Any, target: Any) {
        (source as NMSItemStack).applyComponents((target as NMSItemStack).components)
    }

}

/**
 * 1.20.4-
 *
 * 代码参考自: Taboolib/nms-tag-legacy
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMSItemImpl1 : NMSItem() {

    override fun getTag(nmsItem: Any): NbtCompound? {
        return nmsTagField.get(nmsItem)?.let { NbtHelper.fromNms(it) } as? NbtCompound
    }

    override fun setTag(nmsItem: Any, tag: NbtCompound) {
        nmsTagField.set(nmsItem, NbtHelper.toNms(tag))
    }

    override fun copyItem(nmsItem: Any): Any {
        return nmsCloneMethod.invoke(nmsItem)!!
    }

    override fun applyComponents(source: Any, target: Any) {
        val targetTag = getTag(target) ?: return
        val sourceTag = getTag(source) ?: return
        sourceTag.mergeShallow(targetTag, true)
        setTag(sourceTag, targetTag)
    }

    /**
     * private NBTTagCompound A
     * private NBTTagCompound tag
     */
    val nmsTagField by lazy {
        ReflexClass.of(RefItemStack.nmsClass).structure.getField(if (MinecraftVersion.isUniversal) "A" else "tag")
    }

    /**
     * public nms.ItemStack p()
     * public nms.ItemStack cloneItemStack()
     * public ItemStack s()
     */
    val nmsCloneMethod by lazy {
        ReflexClass.of(RefItemStack.nmsClass).structure.getMethodByType(
            if (MinecraftVersion.versionId >= 12005) "s"
            else if (MinecraftVersion.isUniversal) "p"
            else "cloneItemStack"
        )
    }

}