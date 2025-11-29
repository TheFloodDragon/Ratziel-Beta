package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.impl.component.ItemComponentData
import cn.fd.ratziel.module.item.impl.component.NamespacedIdentifier
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy

/**
 * NMSComponent
 * 
 * @author TheFloodDragon
 * @since 2025/11/29 21:35
 */
abstract class NMSComponent {

    /**
     * 获取组件数据
     */
    @Deprecated("Will be edited")
    abstract fun getComponent(nmsItem: Any, type: NamespacedIdentifier): ItemComponentData?

    /**
     * 设置组件数据
     */
    @Deprecated("Will be edited")
    abstract fun setComponent(nmsItem: Any, type: NamespacedIdentifier, data: ItemComponentData): Boolean

    companion object {

        @JvmStatic
        val INSTANCE by lazy {
            if (MinecraftVersion.versionId >= 12005)
                nmsProxy<NMSItem>("{name}Impl2")
            else nmsProxy<NMSItem>("{name}Impl1")
        }

    }

}

@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMSComponentImpl1 : NMSComponent() {

    override fun getComponent(nmsItem: Any, type: NamespacedIdentifier): ItemComponentData? {
        val root = NMSItem.INSTANCE.getTag(nmsItem) ?: return null
        val value = root[type.key] // 低版本不管命名空间
            ?: return ItemComponentData.removed()
        return ItemComponentData.of(value.clone())
    }

    override fun setComponent(nmsItem: Any, type: NamespacedIdentifier, data: ItemComponentData): Boolean {
        val root = NMSItem.INSTANCE.getTag(nmsItem) ?: NbtCompound().also {
            NMSItem.INSTANCE.setTag(nmsItem, it) // 没有根标签则创建并设置
        }
        val value = data.tag?.clone() ?: return false
        // 设置组件数据
        root[type.key] = value
        return true
    }

}
