package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.nbt.DynamicOpsNBT
import net.minecraft.nbt.NBTBase
import net.minecraft.resources.RegistryOps
import net.minecraft.world.item.component.CustomData
import org.bukkit.craftbukkit.v1_21_R4.CraftRegistry
import taboolib.library.reflex.ReflexClass
import kotlin.jvm.optionals.getOrNull
import net.minecraft.world.item.ItemStack as NMSItemStack

/**
 * NMSItemImpl2 - 1.20.5+
 *
 * @author TheFloodDragon
 * @since 2025/8/4 00:00
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