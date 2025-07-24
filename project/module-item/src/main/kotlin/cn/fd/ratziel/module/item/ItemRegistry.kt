package cn.fd.ratziel.module.item

import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.exception.ComponentNotFoundException
import cn.fd.ratziel.module.item.impl.builder.DefaultResolver
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
    @JvmField
    val registry: MutableList<ComponentIntegrated<*>> = CopyOnWriteArrayList()

    /**
     * 物品解释器注册表
     */
    @JvmField
    val interpreters: MutableList<InterpreterIntegrated<*>> = CopyOnWriteArrayList()

    /**
     * 物品源列表
     */
    @JvmField
    val sources: MutableList<ItemSource> = CopyOnWriteArrayList()

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
    @JvmOverloads
    inline fun <reified T : ItemInterpreter> registerInterpreter(priority: Int = 0, interpreter: Supplier<T>) {
        interpreters.add(InterpreterIntegrated(T::class.java, interpreter, priority))
    }

    /**
     * 注册物品解释器
     */
    @JvmStatic
    @JvmOverloads
    fun registerInterpreter(interpreter: ItemInterpreter, priority: Int = 0) {
        interpreters.add(InterpreterIntegrated(interpreter::class.java, { interpreter }, priority))
    }

    /**
     * 获取物品解释器列表 (排序并获取完实例的列表)
     */
    @JvmStatic
    fun getInterpreterInstances(): List<ItemInterpreter> {
        // 按照优先级排序
        return interpreters.sortedBy { it.priority }.map { it.getter.get() }
    }

    /**
     * 注册物品源
     */
    @JvmStatic
    fun registerSource(source: ItemSource) {
        // 注册物品源名称以便其参与解析
        if (source is ItemSource.Named) {
            DefaultResolver.accessibleNodes.addAll(source.names)
        }
        this.sources.add(source)
    }

    /**
     * 物品解释器集成
     */
    class InterpreterIntegrated<T : ItemInterpreter>(
        /** 物品解释器类型 **/
        val type: Class<out T>,
        /** 物品解释器构建器 **/
        val getter: Supplier<T>,
        /** 物品解释器优先级 **/
        val priority: Int,
    )

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