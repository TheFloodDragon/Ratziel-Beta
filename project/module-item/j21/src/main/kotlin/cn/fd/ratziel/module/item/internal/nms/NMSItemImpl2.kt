package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.impl.component.ItemComponentData
import cn.fd.ratziel.module.item.impl.component.NamespacedIdentifier
import com.mojang.serialization.DataResult
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.DynamicOpsNBT
import net.minecraft.nbt.NBTBase
import net.minecraft.resources.MinecraftKey
import net.minecraft.resources.RegistryOps
import net.minecraft.world.item.component.CustomData
import org.bukkit.craftbukkit.v1_21_R4.CraftRegistry
import taboolib.common.platform.function.severe
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

    override fun getComponent(nmsItem: Any, namespacedKey: NamespacedIdentifier): ItemComponentData? {
        @Suppress("UNCHECKED_CAST")
        val dct = typeByName(namespacedKey) as DataComponentType<Any>
        val data = (nmsItem as NMSItemStack).componentsPatch.get(dct) ?: return null
        val input = data.getOrNull() ?: return null
        // 返回数据
        return ItemComponentData.lazyGetter(
            namespacedKey,
            data.isEmpty
        ) {
            try {
                val result = if (isModern) {
                    dct.codecOrThrow().encodeStart(modernOps, input)
                } else {
                    dct.codecOrThrow().encodeStart(nmsOps, input).map {
                        NMSNbt.INSTANCE.fromNms(it)
                    }
                }
                result.getPartialOrThrow { error("Failed to save: $it") }
            } catch (ex: Throwable) {
                severe(ex.stackTraceToString()); null
            }
        }
    }

    override fun setComponent(nmsItem: Any, data: ItemComponentData): Boolean {
        @Suppress("UNCHECKED_CAST")
        val dct = typeByName(data.type) as DataComponentType<Any>
        // 删除组件 (仅明确标记删除)
        if (data.removed) {
            (nmsItem as NMSItemStack).remove(dct)
        } else {
            // 标签不存在则不处理
            val input = data.tag ?: return false
            val value = try {
                val result = if (isModern) {
                    dct.codecOrThrow().parse(modernOps, input)
                } else {
                    dct.codecOrThrow().parse(nmsOps, NMSNbt.INSTANCE.toNms(input) as NBTBase)
                }
                result.getPartialOrThrow { error("Failed to save: $it") }
            } catch (ex: Throwable) {
                severe(ex.stackTraceToString()); return false
            }
            // 设置组件
            (nmsItem as NMSItemStack).set(dct, value)
        }
        return true
    }

    fun typeByName(namespacedKey: NamespacedIdentifier): DataComponentType<*> {
        val minecraftKey = MinecraftKey.fromNamespaceAndPath(namespacedKey.namespace, namespacedKey.key)
        return BuiltInRegistries.DATA_COMPONENT_TYPE.get(minecraftKey)
            .getOrNull()?.value() ?: error("DataComponentType by '${minecraftKey.path}' not found.")
    }

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
    }

}