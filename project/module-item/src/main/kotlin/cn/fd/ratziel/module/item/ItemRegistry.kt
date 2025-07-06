package cn.fd.ratziel.module.item

import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.exception.ComponentNotFoundException
import cn.fd.ratziel.module.item.impl.builder.provided.SourceInterpreter
import kotlinx.serialization.KSerializer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Supplier

/**
 * ItemRegistry - 物品注册表
 *
 * @author TheFloodDragon
 * @since 2024/10/1 15:03
 */
object ItemRegistry {

    /**
     * 组件集成注册表
     */
    val registry: MutableList<ComponentIntegrated<*>> = CopyOnWriteArrayList()

    /**
     * 物品解释器注册表
     */
    val interpreters: MutableList<Supplier<ItemInterpreter>> = CopyOnWriteArrayList()

    /**
     * 注册组件
     *
     * @param serializer 组件序列化器
     */
    @JvmStatic
    fun <T> registerComponent(
        type: Class<T>,
        serializer: KSerializer<T>,
    ) {
        val integrated = ComponentIntegrated(type, serializer)
        registry.add(integrated)
    }

    /**
     * 获取组件集成构建器
     */
    @JvmStatic
    fun <T> getComponent(type: Class<T>): ComponentIntegrated<T> {
        val integrated = registry.find { it.type == type }
            ?: throw ComponentNotFoundException(type)
        @Suppress("UNCHECKED_CAST")
        return integrated as ComponentIntegrated<T>
    }

    /**
     * 注册物品解释器
     */
    @JvmStatic
    fun registerInterpreter(interpreter: Supplier<ItemInterpreter>) {
        interpreters.add(interpreter)
    }

    /**
     * 注册物品源
     */
    @JvmStatic
    fun registerSource(source: ItemSource) {
        val interpreter = SourceInterpreter(source)
        registerInterpreter { interpreter }
    }

    /**
     * 物品组件集成
     * @param serializer 序列化器
     */
    class ComponentIntegrated<T>(
        /** 物品组件类型 **/
        val type: Class<T>,
        /** 物品组件序列化器 **/
        val serializer: KSerializer<T>,
    )

}