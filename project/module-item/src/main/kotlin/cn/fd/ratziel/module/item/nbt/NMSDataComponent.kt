package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.function.util.uncheck
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.MinecraftKey
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.nmsProxy

/**
 * NMSDataComponent
 *
 * 用于 1.20.5+ 的 [PatchedDataComponentMap] (物品堆叠组件)
 *
 * @author TheFloodDragon
 * @since 2024/5/5 12:39
 */
abstract class NMSDataComponent {

    abstract fun get(pdc: Any, type: Any): Any?

    abstract fun set(pdc: Any, type: Any, value: Any): Any?

    abstract fun clone(pdc: Any): Any

    abstract fun search(name: String): Any?

    abstract fun new(): Any

    companion object {

        /**
         * [net.minecraft.core.component.PatchedDataComponentMap]
         */
        val nmsClass by lazy { nmsClass("PatchedDataComponentMap") }

        val INSTANCE by lazy {
            if (MinecraftVersion.majorLegacy < 12005) throw UnsupportedOperationException("NMSDataComponent is only available after Minecraft 1.20.5!")
            nmsProxy<NMSDataComponent>()
        }

    }

}

class NMSDataComponentImpl : NMSDataComponent() {

    override fun new(): Any = PatchedDataComponentMap(DataComponentMap.EMPTY)

    override fun get(pdc: Any, type: Any): Any? = (pdc as PatchedDataComponentMap).get(type as DataComponentType<*>)

    override fun set(pdc: Any, type: Any, value: Any): Any? = (pdc as PatchedDataComponentMap).set(uncheck<DataComponentType<in Any>>(type), value)

    override fun clone(pdc: Any): Any = (pdc as PatchedDataComponentMap).copy()

    override fun search(name: String): Any? {
        for (entry in typeMap) {
            if (entry.key.path.equals(name)) return entry.value
        }
        return null
    }

    val typeMap: Map<MinecraftKey, DataComponentType<*>> by lazy {
        buildMap {
            val registry = BuiltInRegistries.DATA_COMPONENT_TYPE
            registry.forEach {
                put(registry.getKey(it)!!, it)
            }
        }
    }

}