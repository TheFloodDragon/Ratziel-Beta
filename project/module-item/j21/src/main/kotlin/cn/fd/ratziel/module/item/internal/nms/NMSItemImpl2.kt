package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import com.mojang.serialization.DataResult
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.nbt.DynamicOpsNBT
import net.minecraft.nbt.NBTBase
import net.minecraft.resources.RegistryOps
import net.minecraft.world.item.component.CustomData
import org.bukkit.craftbukkit.v1_21_R4.CraftRegistry
import taboolib.common.platform.function.severe
import taboolib.library.reflex.ReflexClass
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

    val nmsOps: RegistryOps<NBTBase> by lazy {
        CraftRegistry.getMinecraftRegistry().createSerializationContext(DynamicOpsNBT.INSTANCE)
    }

    val modernOps: RegistryOps<NbtTag> by lazy {
        CraftRegistry.getMinecraftRegistry().createSerializationContext(ModernNbtOps)
    }

    fun parse(tag: NbtTag): DataComponentPatch {
        val result: DataResult<DataComponentPatch> = if (isModern) {
            DataComponentPatch.CODEC.parse(modernOps, tag)
        } else {
            val nmsTag = NMSNbt.INSTANCE.toNms(tag) as NBTBase
            DataComponentPatch.CODEC.parse(nmsOps, nmsTag)
        }
        return result.getPartialOrThrow { error("Failed to parse: $it") }
    }

    fun save(patch: DataComponentPatch): NbtTag {
        val result: DataResult<NbtTag> = if (isModern) {
            DataComponentPatch.CODEC.encodeStart(modernOps, patch)
        } else {
            DataComponentPatch.CODEC.encodeStart(nmsOps, patch).map {
                NMSNbt.INSTANCE.fromNms(it)
            }
        }
        return result.getPartialOrThrow { error("Failed to save: $it") }
    }

    override fun getTag(nmsItem: Any): NbtCompound? {
        val patch = (nmsItem as NMSItemStack).componentsPatch
        try {
            val result = save(patch)
            if (result is NbtCompound) {
                return result
            } else {
                severe("Invalid type ${result.type.name} of tag: $result")
            }
        } catch (ex: Throwable) {
            severe(ex.stackTraceToString())
        }
        return null
    }

    override fun setTag(nmsItem: Any, tag: NbtCompound) {
        val patch = try {
            parse(tag)
        } catch (ex: Throwable) {
            severe(ex.stackTraceToString())
            return
        }
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