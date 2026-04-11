package cn.fd.ratziel.module.item.internal.nms

import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.remap.DynamicOpcode.INVOKESTATIC
import taboolib.module.nms.remap.DynamicOpcode.INVOKEVIRTUAL
import taboolib.module.nms.remap.dynamic
import java.util.Optional
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
        @Suppress("UNCHECKED_CAST")
        val opt = if (MinecraftVersion.isUnobfuscated) {
            val minecraftKey = dynamic(
                INVOKESTATIC,
                "net.minecraft.resources.Identifier#tryParse(java.lang.String;)net.minecraft.resources.Identifier;",
                key
            ) ?: error("Invalid MinecraftKey: $key")
            dynamic(
                INVOKEVIRTUAL,
                "net.minecraft.core.Registry#get(net.minecraft.resources.Identifier;)java.util.Optional;",
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                minecraftKey
            ) as Optional<Any>
        } else {
            val minecraftKey = dynamic(
                INVOKESTATIC,
                "net.minecraft.resources.ResourceLocation#tryParse(java.lang.String;)net.minecraft.resources.ResourceLocation;",
                key
            ) ?: error("Invalid MinecraftKey: $key")
            dynamic(
                INVOKEVIRTUAL,
                "net.minecraft.core.Registry#get(net.minecraft.resources.ResourceLocation;)java.util.Optional;",
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                minecraftKey
            ) as Optional<Any>
        }

        return opt.getOrNull() ?: error("DataComponentType not found for key: $key")
    }

}
