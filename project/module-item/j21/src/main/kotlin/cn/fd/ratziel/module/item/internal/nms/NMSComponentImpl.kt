package cn.fd.ratziel.module.item.internal.nms

import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.remap.DynamicOpcode
import taboolib.module.nms.remap.dynamic
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * NMSComponentImpl
 * 
 * @author TheFloodDragon
 * @since 2026/4/11 22:43
 */
@Suppress("unused")
class NMSComponentImpl : NMSComponent() {

    override fun getComponent(nmsItem: Any, type: Any): Any? {
        nmsItem as ItemStack
        @Suppress("UNCHECKED_CAST")
        type as DataComponentType<Any>
        return nmsItem.get(type)
    }

    override fun setComponent(nmsItem: Any, type: Any, component: Any) {
        nmsItem as ItemStack
        @Suppress("UNCHECKED_CAST")
        type as DataComponentType<Any>
        nmsItem.set(type, component)
    }

    override fun removeComponent(nmsItem: Any, type: Any) {
        nmsItem as ItemStack
        @Suppress("UNCHECKED_CAST")
        type as DataComponentType<*>
        nmsItem.remove(type)
    }

    override fun getType(key: String): Any {
        val opt: Optional<*> = if (MinecraftVersion.isUnobfuscated) {
            val minecraftKey = net.minecraft.resources.Identifier.tryParse(key) ?: error("Invalid MinecraftKey: $key")
            BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(minecraftKey)
        } else {
            val minecraftKey = net.minecraft.resources.MinecraftKey.tryParse(key) ?: error("Invalid MinecraftKey: $key")
            @Suppress("UNCHECKED_CAST") val type = dynamic(
                DynamicOpcode.GETSTATIC,
                "net.minecraft.core.registries.BuiltInRegistries#DATA_COMPONENT_TYPE:net.minecraft.core.IRegistry",
            ) as net.minecraft.core.IRegistry<DataComponentType<*>>
            dynamic(
                DynamicOpcode.INVOKEVIRTUAL,
                "net.minecraft.core.IRegistry#get(net.minecraft.resources.MinecraftKey;)java.util.Optional;",
                type, minecraftKey
            ) as Optional<*>
        }
        return opt.getOrNull() ?: error("DataComponentType not found for key: $key")
    }

}
