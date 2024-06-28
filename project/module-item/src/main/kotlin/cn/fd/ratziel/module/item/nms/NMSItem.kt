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
    abstract fun getItemTag(nmsItem: Any): NBTCompound?

    /**
     * 设置 [NMSItemStack]的 NBT (克隆)
     * @param nbt [NBTTagCompound]
     */
    abstract fun setItemTag(nmsItem: Any, nbt: NBTCompound)

    /**
     * 1.20.5+
     * [getItemTag] 是忽略掉默认值的
     * 而 [getItemTagWithDefault] 会带上默认值
     */
    open fun getItemTagWithDefault(nmsItem: Any): NBTCompound? = getItemTag(nmsItem)

    /**
     * 1.20.5+
     * [setItemTag] 是忽略掉默认值的
     * 而 [setItemTagWithDefault] 会带上默认值
     */
    open fun setItemTagWithDefault(nmsItem: Any, nbt: NBTCompound) = setItemTag(nmsItem, nbt)

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

    override fun getItemTag(nmsItem: Any): NBTCompound? =
        RefItemStack.InternalUtil.nmsTagField.get(nmsItem)?.let { NBTCompound(it).clone() }

    override fun setItemTag(nmsItem: Any, nbt: NBTCompound) =
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

    override fun getItemTag(nmsItem: Any): NBTCompound? {
        val dcp = (nmsItem as NMSItemStack).componentsPatch
        return NMS12005.INSTANCE.savePatch(dcp)?.let { NBTCompound(it) }
    }

    override fun setItemTag(nmsItem: Any, nbt: NBTCompound) {
        val dcp = NMS12005.INSTANCE.parsePatch(nbt.getData() as NBTTagCompound) as? DataComponentPatch
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

    override fun getItemTagWithDefault(nmsItem: Any): NBTCompound? {
        val tag = getItemTag(nmsItem)
        val default = getItemBasicNBT(nmsItem) ?: return tag
        return tag?.merge(NBTCompound(default), false)
    }

    override fun setItemTagWithDefault(nmsItem: Any, nbt: NBTCompound) {
        var tag = nbt
        val default = getItemBasicNBT(nmsItem)
        if (default != null) tag = NBTCompound(default).merge(nbt, true)
        setItemTag(nmsItem, tag)
    }

    val cache: MutableMap<Int, NBTTagCompound> = ConcurrentHashMap()

    fun getItemBasicNBT(nmsItem: Any): NBTTagCompound? {
        val item = (nmsItem as NMSItemStack).item // 获取物品类
        val id = net.minecraft.world.item.Item.getId(item) // 获取物品ID
        val result = cache[id] // 尝试通过缓存
        if (result != null) return result
        // 生成并加入到缓存
        val nbt = NMS12005.INSTANCE.saveMap(item.components()) as? NBTTagCompound
        if (nbt != null) {
            cache[id] = nbt
            return nbt
        }
        return null
    }

}