package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.nbt.DynamicOpsNBT
import net.minecraft.nbt.NBTBase
import net.minecraft.resources.RegistryOps
import net.minecraft.world.item.component.CustomData
import org.bukkit.craftbukkit.v1_21_R4.CraftRegistry
import taboolib.common.platform.function.severe
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import net.minecraft.world.item.ItemStack as NMSItemStack

/**
 * NMSItemImpl2 - 1.20.5+
 *
 * @author TheFloodDragon
 * @since 2025/8/4 00:00
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMSItemImpl2 : NMSItem() {

    /**
     * 是否支持最新的 NbtOps 解析 (1.20.5+ : 物品数据改组件存之后)
     * Tips: 这玩意会随版本变, 所以只能支持最后一个修改的版本及以上
     */
    private val isModern = MinecraftVersion.versionId >= MODERN_VERSION

    val nmsOps: RegistryOps<NBTBase> by lazy {
        CraftRegistry.getMinecraftRegistry().createSerializationContext(DynamicOpsNBT.INSTANCE)
    }

    val modernOps: RegistryOps<NbtTag> by lazy {
        CraftRegistry.getMinecraftRegistry().createSerializationContext(ModernNbtOps)
    }

    val customDataConstructor by lazy {
        ReflexClass.of(CustomData::class.java).structure.getConstructorByType(NBTTagCompound::class.java)
    }

    val componentsField by lazy {
        ReflexClass.of(NMSItemStack::class.java).getField("components", remap = true)
    }

    override fun getTag(nmsItem: Any): NbtCompound? {
        val patch = (nmsItem as NMSItemStack).componentsPatch
        try {
            val result = saveToTag(DataComponentPatch.CODEC, patch)
            if (result is NbtCompound) {
                return result
            } else {
                severe("Invalid tag result: $result")
            }
        } catch (ex: Throwable) {
            severe(ex.stackTraceToString())
        }
        return null
    }

    override fun setTag(nmsItem: Any, tag: NbtCompound) {
        val patch = try {
            parseFromTag(DataComponentPatch.CODEC, tag)
        } catch (ex: Throwable) {
            severe(ex.stackTraceToString())
            return
        }
        // 设置组件数据
        setPatch(nmsItem, patch)
    }

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
    }

    override fun rewriteTag(nmsItem: Any, target: Any?) {
        // 目标物品组件数据
        val patch = (target as? NMSItemStack)?.componentsPatch ?: DataComponentPatch.EMPTY
        // 设置组件数据
        setPatch(nmsItem, patch)
    }

    fun setPatch(nmsItem: Any, patch: DataComponentPatch) {
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

    fun <T> parseFromTag(codec: Codec<T>, tag: NbtTag): T {
        val result: DataResult<T> = if (isModern) {
            codec.parse(modernOps, tag)
        } else {
            val nmsTag = NMSNbt.INSTANCE.toNms(tag) as NBTBase
            codec.parse(nmsOps, nmsTag)
        }
        return result.getOrThrow { IllegalStateException("Failed to parse: $it") }
    }

    fun <T> saveToTag(codec: Codec<T>, value: T): NbtTag {
        val result: DataResult<NbtTag> = if (isModern) {
            codec.encodeStart(modernOps, value)
        } else {
            codec.encodeStart(nmsOps, value).map {
                NMSNbt.INSTANCE.fromNms(it)
            }
        }
        return result.getOrThrow { IllegalStateException("Failed to save: $it") }
    }

}