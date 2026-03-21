package cn.fd.ratziel.module.item.internal.nms

import cn.fd.ratziel.core.exception.UnsupportedVersionException
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.MinecraftKey
import net.minecraft.world.item.ItemStack
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy
import kotlin.jvm.optionals.getOrNull

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
     * 通过 [MinecraftKey] 形式的 ID 获取 [DataComponentType]
     */
    abstract fun getType(key: String): Any

    companion object {

        @JvmStatic
        val INSTANCE by lazy {
            if (MinecraftVersion.versionId >= 12005)
                nmsProxy<NMSComponent>("{name}Impl")
            else throw UnsupportedVersionException("NMSComponent only supports Minecraft 1.20.5+")
        }

    }

}

@Suppress("unused")
class NMSComponentImpl : NMSComponent() {

    override fun getComponent(nmsItem: Any, type: Any): Any? {
        nmsItem as ItemStack
        @Suppress("UNCHECKED_CAST")
        type as DataComponentType<Any?>
        return nmsItem.get(type)
    }

    override fun setComponent(nmsItem: Any, type: Any, component: Any) {
        nmsItem as ItemStack
        @Suppress("UNCHECKED_CAST")
        type as DataComponentType<Any?>
        nmsItem.set(type, component)
    }

    override fun removeComponent(nmsItem: Any, type: Any) {
        nmsItem as ItemStack
        @Suppress("UNCHECKED_CAST")
        type as DataComponentType<*>
        nmsItem.remove(type)
    }

    override fun getType(key: String): Any {
        val minecraftKey = MinecraftKey.tryParse(key) ?: error("Invalid MinecraftKey: $key")
        val opt = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(minecraftKey)
        return opt.getOrNull() ?: error("DataComponentType not found for key: $key")
    }

}
