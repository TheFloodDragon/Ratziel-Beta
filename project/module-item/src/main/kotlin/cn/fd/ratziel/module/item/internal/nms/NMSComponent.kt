package cn.fd.ratziel.module.item.internal.nms

import cn.fd.ratziel.core.exception.UnsupportedVersionException
import net.minecraft.core.component.DataComponentType
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
     * 获取组件
     *
     * @param nmsItem [net.minecraft.world.item.ItemStack]
     * @param type [net.minecraft.core.component.DataComponentType]
     */
    abstract fun getComponent(nmsItem: Any, type: Any): Any?

    /**
     * 设置组件
     *
     * @param nmsItem [net.minecraft.world.item.ItemStack]
     * @param type [net.minecraft.core.component.DataComponentType]
     * @param component Minecraft 组件数据对象
     */
    abstract fun setComponent(nmsItem: Any, type: Any, component: Any)

    /**
     * 移除组件
     *
     * @param nmsItem [net.minecraft.world.item.ItemStack]
     * @param type [net.minecraft.core.component.DataComponentType]
     */
    abstract fun removeComponent(nmsItem: Any, type: Any)

    /**
     * 通过 [net.minecraft.resources.MinecraftKey] 形式的 ID 获取 [DataComponentType]
     */
    abstract fun getType(key: String): Any

    companion object {

        @JvmField
        val isEnabled = MinecraftVersion.versionId >= 12005

        @JvmStatic
        val INSTANCE by lazy {
            if (!isEnabled) throw UnsupportedVersionException("NMSComponent only supports Minecraft 1.20.5+")
            nmsProxy<NMSComponent>("{name}Impl")
        }

    }

}
