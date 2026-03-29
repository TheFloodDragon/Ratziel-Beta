package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.component.transformer.MinecraftTransformer
import cn.fd.ratziel.module.item.internal.nms.NMSComponent

/**
 * MinecraftHandleTransformer
 *
 * 仅支持 1.20.5+
 * 
 * @author TheFloodDragon
 * @since 2026/3/21 23:17
 */
open class MinecraftHandleTransformer<T>(
    /** 组件类型 ID (Minecraft 内部命名) **/
    typeKey: String,
    /** 封装对象 -> Minecraft 组件对象 转换器 **/
    val e2mTransformer: MinecraftE2MTransformer<T>,
) : MinecraftTransformer<T> {

    /** [net.minecraft.core.component.DataComponentType] **/
    private val dct = NMSComponent.INSTANCE.getType(typeKey)

    override fun read(nmsItem: Any): T? {
        val minecraftObj = NMSComponent.INSTANCE.getComponent(nmsItem, dct) ?: return null
        return e2mTransformer.fromMinecraftObj(minecraftObj)
    }

    override fun write(nmsItem: Any, component: T) {
        val minecraftObj = e2mTransformer.toMinecraftObj(component)
        NMSComponent.INSTANCE.setComponent(nmsItem, dct, minecraftObj)
    }

    override fun remove(nmsItem: Any) {
        NMSComponent.INSTANCE.removeComponent(nmsItem, dct)
    }

    override fun toString() = "MinecraftHandleTransformer(type=$dct, transformer=$e2mTransformer)"

}