package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.impl.component.ItemComponentData
import cn.fd.ratziel.module.item.impl.component.NamespacedIdentifier
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy
import net.minecraft.server.v1_12_R1.NBTTagCompound as NBTTagCompound12
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
     * 获取组件数据
     */
    abstract fun getComponent(nmsItem: Any, type: NamespacedIdentifier): ItemComponentData?

    /**
     * 设置组件数据
     */
    abstract fun setComponent(nmsItem: Any, type: NamespacedIdentifier, data: ItemComponentData): Boolean

    /**
     * 克隆 [NMSItemStack]
     */
    abstract fun copyItem(nmsItem: Any): Any

    companion object {

        /**
         * 是否支持最新的 NbtOps 解析 (1.20.5+ : 物品数据改组件存之后)
         * Tips: 这玩意会随版本变, 所以只能支持最后一个修改的版本及以上
         */
        @JvmStatic
        val isModern = MinecraftVersion.versionId >= 12105

        @JvmStatic
        val INSTANCE by lazy {
            if (MinecraftVersion.versionId >= 12005)
                nmsProxy<NMSItem>("{name}Impl2")
            else nmsProxy<NMSItem>("{name}Impl1")
        }

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
        return nmsTagField.get(nmsItem)?.let { NMSNbt.INSTANCE.fromNms(it) } as? NbtCompound
    }

    override fun setTag(nmsItem: Any, tag: NbtCompound) {
        nmsTagField.set(nmsItem, NMSNbt.INSTANCE.toNms(tag))
    }

    override fun getComponent(nmsItem: Any, type: NamespacedIdentifier): ItemComponentData? {
        val root = getTag(nmsItem) ?: return null
        val value = root[type.key] // 低版本不管命名空间
            ?: return ItemComponentData.removed()
        return ItemComponentData.of(value.clone())
    }

    override fun setComponent(nmsItem: Any, type: NamespacedIdentifier, data: ItemComponentData): Boolean {
        val root = getTag(nmsItem) ?: NbtCompound().also {
            setTag(nmsItem, it) // 没有根标签则创建并设置
        }
        val value = data.tag?.clone() ?: return false
        // 设置组件数据
        root[type.key] = value
        return true
    }

    override fun copyItem(nmsItem: Any): Any {
        return nmsCloneMethod.invoke(nmsItem)!!
    }

    /**
     * private NBTTagCompound tag
     */
    val nmsTagField by lazy {
        ReflexClass.of(RefItemStack.nmsClass).getField("tag", remap = true)
    }

    /**
     * public nms.ItemStack copy()
     * public nms.ItemStack cloneItemStack()
     */
    val nmsCloneMethod by lazy {
        ReflexClass.of(RefItemStack.nmsClass).getMethod(
            if (MinecraftVersion.isUniversal) "copy"
            else "cloneItemStack", remap = true
        )
    }

    val srcMapField by lazy {
        ReflexClass.of(NBTTagCompound12::class.java).getField(if (MinecraftVersion.isUniversal) "tags" else "map", remap = true)
    }

}