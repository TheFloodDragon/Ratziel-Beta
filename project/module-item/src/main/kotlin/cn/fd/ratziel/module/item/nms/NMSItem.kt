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
     * 1.20.5+
     * 通过合并部分物品默认组件的方式, 增强兼容性
     */
    open fun getTagHandled(nmsItem: Any): NBTCompound? = getTag(nmsItem)

    /**
     * 1.20.5+
     * 通过合并部分物品默认组件的方式, 增强兼容性
     */
    open fun setTagHandled(nmsItem: Any, tag: NBTCompound) = setTag(nmsItem, tag)

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

    override fun getTagHandled(nmsItem: Any): NBTCompound {
        // 获取未处理的标签
        val handle = getTag(nmsItem)
        // 获取默认标签
        val default = NBTCompound(getDefaultTag(nmsItem))
        // 如果为空物品则返回默认标签
        if (handle == null) return default
        // 遍历并合并 NBTCompound
        merge(handle, default)
        return handle // 返回结果
    }

    override fun setTagHandled(nmsItem: Any, tag: NBTCompound) {
        // 获取未处理的标签 (并克隆)
        val handle = tag.clone()
        // 获取默认标签
        val default = NBTCompound(getDefaultTag(nmsItem))
        // 遍历并合并 NBTCompound
        merge(handle, default)
        // 设置NBT标签
        setTag(nmsItem, handle)
    }

    fun merge(handle: NBTCompound, default: NBTCompound) {
        val defaultNodes = default.keys
        // 遍历并合并 NBTCompound
        for (entry in handle) {
            if (defaultNodes.contains(entry.key)) {
                val defaultValue = default[entry.key] as? NBTCompound ?: continue
                val handleValue = entry.value as? NBTCompound ?: continue
                handleValue.merge(defaultValue, false)
            }
        }
    }

    /**
     * 物品默认组件转化成NBT后的缓存
     */
    val cache: MutableMap<Int, NBTTagCompound> = ConcurrentHashMap()

    /**
     * 获取物品默认NBT标签 (经过缓存)
     */
    fun getDefaultTag(nmsItem: Any): NBTTagCompound {
        val type = (nmsItem as NMSItemStack).item // 获取物品类型
        val id = NMSItemType.getId(type) // 获取物品ID
        // 尝试通过缓存获取
        val result = cache[id]
        if (result != null) return result
        // 缓存中不存在时, 生成并加入到缓存
        val tag = NMS12005.INSTANCE.saveMap(type.components()) as NBTTagCompound
        cache[id] = tag // 加入缓存
        return tag // 返回结果
    }

}