package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.NMSItemStack
import cn.fd.ratziel.module.nbt.NBTHelper
import cn.fd.ratziel.module.nbt.NBTTagCompound
import cn.fd.ratziel.module.nbt.NBTTagCompound12
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.world.item.component.CustomData
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy

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
     * 获取 [NMSItemStack] 的自定义 NBT (克隆)
     */
    abstract fun getCustomTag(nmsItem: Any): NbtCompound?

    /**
     * 设置 [NMSItemStack] 的自定义 NBT (克隆)
     */
    abstract fun setCustomTag(nmsItem: Any, tag: NbtCompound)

    /**
     * 克隆 [NMSItemStack]
     */
    abstract fun copyItem(nmsItem: Any): Any

    /**
     * 合并内部标签数据
     */
    abstract fun mergeTag(source: Any, target: Any)

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

    val customDataConstructor by lazy {
        ReflexClass.of(CustomData::class.java).structure.getConstructorByType(NBTTagCompound::class.java)
    }

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

    override fun getCustomTag(nmsItem: Any): NbtCompound? {
        val customData = (nmsItem as NMSItemStack).get(DataComponents.CUSTOM_DATA)
        @Suppress("DEPRECATION")
        return customData?.unsafe?.let { NBTHelper.fromNms(it) } as? NbtCompound
    }

    override fun setCustomTag(nmsItem: Any, tag: NbtCompound) {
        val customData = customDataConstructor.instance(NBTHelper.toNms(tag))!! as CustomData
        (nmsItem as NMSItemStack).set(DataComponents.CUSTOM_DATA, customData)
    }

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
    }

    override fun mergeTag(source: Any, target: Any) {
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
        return nmsTagField.get(nmsItem)?.let { NBTHelper.fromNms(it) } as? NbtCompound
    }

    override fun setTag(nmsItem: Any, tag: NbtCompound) {
        nmsTagField.set(nmsItem, NBTHelper.toNms(tag))
    }

    override fun getCustomTag(nmsItem: Any): NbtCompound? {
        val nmsTag = nmsTagField.get(nmsItem) ?: return null
        @Suppress("UNCHECKED_CAST") val map = srcMapField.get(nmsTag) as? MutableMap<String, Any> ?: return null
        val customTag = map["custom_data"] as? NBTTagCompound12 ?: return null
        return NBTHelper.fromNms(customTag) as NbtCompound
    }

    override fun setCustomTag(nmsItem: Any, tag: NbtCompound) {
        val nmsTag = nmsTagField.get(nmsItem) ?: return
        @Suppress("UNCHECKED_CAST") val map = srcMapField.get(nmsTag) as? MutableMap<String, Any> ?: return
        map["custom_data"] = NBTHelper.toNms(tag)
    }

    override fun copyItem(nmsItem: Any): Any {
        return nmsCloneMethod.invoke(nmsItem)!!
    }

    override fun mergeTag(source: Any, target: Any) {
        val targetTag = getTag(target) ?: return
        val sourceTag = getTag(source) ?: return
        sourceTag.merge(targetTag)
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

    val srcMapField by lazy {
        ReflexClass.of(net.minecraft.server.v1_12_R1.NBTTagCompound::class.java).getField(if (MinecraftVersion.isUniversal) "x" else "map")
    }

}