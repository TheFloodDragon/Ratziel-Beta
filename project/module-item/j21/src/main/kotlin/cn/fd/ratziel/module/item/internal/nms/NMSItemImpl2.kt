package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.internal.nms.CodecSerialization.parseFromTag
import cn.fd.ratziel.module.item.internal.nms.CodecSerialization.saveToTag
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.world.item.component.CustomData
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

    override fun getTag(nmsItem: Any): NbtCompound? {
        val patch = (nmsItem as NMSItemStack).componentsPatch
        try {
            val result = saveToTag(DataComponentPatch.CODEC, patch)
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
            parseFromTag(DataComponentPatch.CODEC, tag)
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

    override fun copyItem(nmsItem: Any): Any {
        return (nmsItem as NMSItemStack).copy()
    }

}