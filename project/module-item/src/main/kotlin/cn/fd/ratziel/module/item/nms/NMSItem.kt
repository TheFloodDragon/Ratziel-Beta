package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.module.item.api.NMSItemStack
import cn.fd.ratziel.module.nbt.NBTCompound
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
     * @return [NBTCompound]
     */
    abstract fun getTag(nmsItem: Any): NBTCompound?

    /**
     * 设置 [NMSItemStack] 的 NBT (克隆)
     */
    abstract fun setTag(nmsItem: Any, tag: NBTCompound)

    /**
     * 获取 [NMSItemStack] 的自定义 NBT (克隆)
     */
    abstract fun getCustomTag(nmsItem: Any): NBTCompound?

    /**
     * 设置 [NMSItemStack] 的自定义 NBT (克隆)
     */
    abstract fun setCustomTag(nmsItem: Any, tag: NBTCompound)

    /**
     * 克隆 [NMSItemStack]
     */
    abstract fun copyItem(nmsItem: Any): Any

    companion object {

        val INSTANCE by lazy {
            if (MinecraftVersion.majorLegacy >= 12005)
                nmsProxy<NMSItem>("{name}Impl2")
            else nmsProxy<NMSItem>("{name}Impl1")
        }

    }

}

/**
 * 1.20.5+
 */
@Suppress("unused", "MemberVisibilityCanBePrivate", "DEPRECATION")
class NMSItemImpl2 : NMSItem() {

    val customDataConstructor by lazy {
        ReflexClass.of(CustomData::class.java).structure.getConstructorByType(NBTTagCompound::class.java)
    }

    override fun getTag(nmsItem: Any): NBTCompound? {
        val dcp = (nmsItem as NMSItemStack).componentsPatch
        return NMS12005.INSTANCE.savePatch(dcp)
    }

    override fun setTag(nmsItem: Any, tag: NBTCompound) {
        val components = (nmsItem as NMSItemStack).components as? PatchedDataComponentMap ?: return
        val dcp = NMS12005.INSTANCE.parsePatch(tag) as DataComponentPatch
        components.restorePatch(dcp)
    }

    // TODO review
    fun applyTag(nmsItem: Any, tag: NBTCompound) {
        val dcp = NMS12005.INSTANCE.parsePatch(tag) as DataComponentPatch
        (nmsItem as NMSItemStack).applyComponents(dcp)
        // 特殊处理自定义数据
        val customData = dcp[DataComponents.CUSTOM_DATA]
        if (customData?.isPresent == true) {
            val customTag = customData.get().unsafe.let { NBTHelper.fromNms(it) } as? NBTCompound ?: return
            val src = getCustomTag(nmsItem)
            setCustomTag(nmsItem, src?.merge(customTag, true) ?: customTag)
        }
    }

    override fun getCustomTag(nmsItem: Any): NBTCompound? {
        val customData = (nmsItem as NMSItemStack).get(DataComponents.CUSTOM_DATA)
        return customData?.unsafe?.let { NBTHelper.fromNms(it) } as? NBTCompound
    }

    override fun setCustomTag(nmsItem: Any, tag: NBTCompound) {
        val customData = customDataConstructor.instance(NBTHelper.toNms(tag))!! as CustomData
        (nmsItem as NMSItemStack).set(DataComponents.CUSTOM_DATA, customData)
    }

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
    }

}

/**
 * 1.20.4-
 *
 * 代码参考自: Taboolib/nms-tag-legacy
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMSItemImpl1 : NMSItem() {

    override fun getTag(nmsItem: Any): NBTCompound? {
        return nmsTagField.get(nmsItem)?.let { NBTHelper.fromNms(it) } as? NBTCompound
    }

    override fun setTag(nmsItem: Any, tag: NBTCompound) {
        nmsTagField.set(nmsItem, NBTHelper.toNms(tag))
    }

    override fun getCustomTag(nmsItem: Any): NBTCompound? {
        val nmsTag = nmsTagField.get(nmsItem) ?: return null
        @Suppress("UNCHECKED_CAST") val map = srcMapField.get(nmsTag) as? MutableMap<String, Any> ?: return null
        val customTag = map[ItemSheet.CUSTOM_DATA.name] as? NBTTagCompound12 ?: return null
        return NBTHelper.fromNms(customTag) as NBTCompound
    }

    override fun setCustomTag(nmsItem: Any, tag: NBTCompound) {
        val nmsTag = nmsTagField.get(nmsItem) ?: return
        @Suppress("UNCHECKED_CAST") val map = srcMapField.get(nmsTag) as? MutableMap<String, Any> ?: return
        map[ItemSheet.CUSTOM_DATA.name] = NBTHelper.toNms(tag)
    }

    override fun copyItem(nmsItem: Any): Any {
        return nmsCloneMethod.invoke(nmsItem)!!
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
            if (MinecraftVersion.majorLegacy >= 12005) "s"
            else if (MinecraftVersion.isUniversal) "p"
            else "cloneItemStack"
        )
    }

    val srcMapField by lazy {
        ReflexClass.of(net.minecraft.server.v1_12_R1.NBTTagCompound::class.java).getField(if (MinecraftVersion.isUniversal) "x" else "map")
    }

}