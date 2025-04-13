package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.impl.ItemSheet
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.nbt.DynamicOpsNBT
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.resources.RegistryOps
import net.minecraft.world.item.component.CustomData
import org.bukkit.craftbukkit.v1_20_R4.CraftRegistry
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy
import kotlin.jvm.optionals.getOrNull
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

    val ops: RegistryOps<NBTBase> get() = CraftRegistry.getMinecraftRegistry().createSerializationContext(DynamicOpsNBT.INSTANCE)

    override fun getTag(nmsItem: Any): NbtCompound? {
        val patch = (nmsItem as NMSItemStack).componentsPatch
        val nmsTag = DataComponentPatch.CODEC.encodeStart(ops, patch).resultOrPartial {
            error("Failed to save: $it")
        }.getOrNull() ?: return null
        return NMSNbt.INSTANCE.fromNms(nmsTag) as? NbtCompound
    }

    override fun setTag(nmsItem: Any, tag: NbtCompound) {
        val nmsTag = NMSNbt.INSTANCE.toNms(tag) as NBTBase
        // 解析成 DataComponentPatch
        val patch = DataComponentPatch.CODEC.parse(ops, nmsTag).resultOrPartial {
            error("Failed to parse: $it")
        }.getOrNull() ?: return
        // 源物品组件
        val components = (nmsItem as NMSItemStack).components
        // 源物品的组件不为空
        if (components is PatchedDataComponentMap) {
            components.restorePatch(patch)
        } else { // 源物品组件为空
            val newPatchedMap = PatchedDataComponentMap.fromPatch(nmsItem.prototype, patch)
            componentsField.set(nmsItem, newPatchedMap) // 直接设置
        }
    }

    override fun getCustomTag(nmsItem: Any): NbtCompound? {
        val customData = (nmsItem as NMSItemStack).get(DataComponents.CUSTOM_DATA)
        @Suppress("DEPRECATION")
        return customData?.unsafe?.let { NMSNbt.INSTANCE.fromNms(it) } as? NbtCompound
    }

    override fun setCustomTag(nmsItem: Any, tag: NbtCompound) {
        val customData = customDataConstructor.instance(NMSNbt.INSTANCE.toNms(tag))!! as CustomData
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

    override fun getTag(nmsItem: Any): NbtCompound? {
        return nmsTagField.get(nmsItem)?.let { NMSNbt.INSTANCE.fromNms(it) } as? NbtCompound
    }

    override fun setTag(nmsItem: Any, tag: NbtCompound) {
        nmsTagField.set(nmsItem, NMSNbt.INSTANCE.toNms(tag))
    }

    override fun getCustomTag(nmsItem: Any): NbtCompound? {
        val nmsTag = nmsTagField.get(nmsItem) ?: return null
        @Suppress("UNCHECKED_CAST") val map = srcMapField.get(nmsTag) as? MutableMap<String, Any> ?: return null
        val customTag = map[ItemSheet.CUSTOM_DATA_COMPONENT] as? NBTTagCompound12 ?: return null
        return NMSNbt.INSTANCE.fromNms(customTag) as NbtCompound
    }

    override fun setCustomTag(nmsItem: Any, tag: NbtCompound) {
        val nmsTag = nmsTagField.get(nmsItem) as? NBTTagCompound12 ?: return
        @Suppress("UNCHECKED_CAST") val map = srcMapField.get(nmsTag) as? MutableMap<String, Any>
        if (map != null) {
            // 直接对源标签操作
            map[ItemSheet.CUSTOM_DATA_COMPONENT] = NMSNbt.INSTANCE.toNms(tag)
        } else {
            val newTag = NbtCompound { put(ItemSheet.CUSTOM_DATA_COMPONENT, tag) }
                .let { NMSNbt.INSTANCE.toNms(it) } // 转换成 NMS 形式
            // 设置新的标签
            nmsTagField.set(nmsItem, newTag)
        }
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
            if (MinecraftVersion.versionId >= 12005) "s"
            else if (MinecraftVersion.isUniversal) "p"
            else "cloneItemStack"
        )
    }

    val srcMapField by lazy {
        ReflexClass.of(NBTTagCompound12::class.java).getField(if (MinecraftVersion.isUniversal) "x" else "map")
    }

}