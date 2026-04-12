package cn.fd.ratziel.module.item

import cn.fd.ratziel.core.serialization.elementNodes
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.api.exception.ComponentNotFoundException
import cn.fd.ratziel.module.item.feature.dynamic.DynamicTagService
import cn.fd.ratziel.module.item.impl.builder.DefaultResolver
import kotlinx.serialization.KSerializer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Supplier
import kotlin.reflect.KClass

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
    val registry = CopyOnWriteArrayList<ComponentIntegrated<*>>()

    /**
     * 物品解释器注册表
     */
    @JvmField
    val interpreters = CopyOnWriteArrayList<InterpreterIntegrated<*>>()

    /**
     * 物品源列表
     */
    @JvmField
    val sources = CopyOnWriteArrayList<ItemSource>()

    /**
     * 物品解析器列表
     */
    val sectionResolvers = CopyOnWriteArrayList<ItemSectionResolver>()

    /**
     * 默认的静态物品标签解析器列表
     */
    @JvmField
    val staticTagResolvers = CopyOnWriteArrayList<ItemTagResolver>()

    /**
     * 注册组件
     *
     * @param serializer 组件序列化器
     */
    @Deprecated("被新组件系统替代")
    @JvmStatic
    fun <T> registerComponent(type: Class<T>, serializer: KSerializer<T>) {
        registry += ComponentIntegrated(type, serializer)
    }

    /**
     * 获取组件集成构建器
     */
    @Deprecated("被新组件系统替代")
    @JvmStatic
    fun <T> getComponent(type: Class<T>): ComponentIntegrated<T> {
        @Suppress("UNCHECKED_CAST")
        return (registry.find { it.type == type } ?: throw ComponentNotFoundException(type)) as ComponentIntegrated<T>
    }

    /**
     * 注册物品解释器单例
     */
    @JvmStatic
    fun <T : ItemInterpreter> registerInterpreter(
        interpreter: T,
        order: InterpreterOrderSpec.() -> Unit = {},
    ) = registerInterpreter(interpreter::class.java, { interpreter }, order)

    /**
     * 注册物品解释器工厂
     */
    @JvmStatic
    inline fun <reified T : ItemInterpreter> registerInterpreter(
        noinline interpreter: () -> T,
        noinline order: InterpreterOrderSpec.() -> Unit = {},
    ) = registerInterpreter(T::class.java, Supplier(interpreter), order)

    /**
     * 注册物品解释器
     */
    @JvmStatic
    fun <T : ItemInterpreter> registerInterpreter(
        clazz: Class<out T>,
        interpreter: Supplier<T>,
        order: InterpreterOrderSpec.() -> Unit = {},
    ) {
        check(interpreters.none { it.type == clazz }) { "Interpreter ${clazz.simpleName} is already registered." }
        interpreters += InterpreterIntegrated(clazz, interpreter, resolveInterpreterOrder(clazz, InterpreterOrderSpec().apply(order)))
    }

    /**
     * 获取按 order 分组后的物品解释器实例
     */
    @JvmStatic
    fun getInterpreterInstanceGroups(): List<List<ItemInterpreter>> = orderedInterpreterGroups.map { group -> group.map { it.getter.get() } }

    /**
     * 注册物品源
     */
    @JvmStatic
    fun registerSource(source: ItemSource) {
        // 注册物品源名称以便其参与解析
        if (source is ItemSource.Named) DefaultResolver.accessibleNodes.addAll(source.names)
        sources += source
    }

    /**
     * 注册 [ItemSectionResolver]
     */
    @JvmStatic
    fun registerSectionResolver(resolver: ItemSectionResolver) {
        sectionResolvers += resolver
    }

    /**
     * 注册默认的静态物品标签解析器
     */
    @JvmStatic
    fun registerStaticTagResolver(resolver: ItemTagResolver) {
        staticTagResolvers += resolver
    }

    /**
     * 注册默认的动态物品标签解析器
     */
    @JvmStatic
    fun registerDynamicTagResolver(resolver: ItemTagResolver) {
        DynamicTagService.registerResolver(resolver)
    }

    /**
     * 按顺序分组后的解释器集成
     */
    private val orderedInterpreterGroups: List<List<InterpreterIntegrated<*>>>
        get() = interpreters.groupBy { it.order }.toSortedMap().values.map { it.toList() }

    /**
     * 根据 before / after 约束解析解释器层级顺序
     */
    private fun resolveInterpreterOrder(source: Class<out ItemInterpreter>, spec: InterpreterOrderSpec): Int {
        val before = spec.beforeTargets.map { it.findTargetOf(source, "before") }
        val after = spec.afterTargets.map { it.findTargetOf(source, "after") }
        if (before.isEmpty() && after.isEmpty()) return 0
        if (after.isEmpty()) return before.minOf { it.order } - 1
        if (before.isEmpty()) return after.maxOf { it.order } + 1

        val lowerBound = after.maxOf { it.order } + 1
        val upperBound = before.minOf { it.order } - 1
        check(lowerBound <= upperBound) {
            "Interpreter ${source.simpleName} declares incompatible order constraints: " +
                    "after(${after.joinToString(", ") { it.type.simpleName }}) requires >= $lowerBound, " +
                    "before(${before.joinToString(", ") { it.type.simpleName }}) requires <= $upperBound."
        }
        return lowerBound
    }

    /**
     * 解析顺序约束中的目标解释器
     */
    private fun Class<out ItemInterpreter>.findTargetOf(source: Class<out ItemInterpreter>, relation: String): InterpreterIntegrated<*> {
        check(this != source) { "Interpreter ${source.simpleName} cannot declare $relation($simpleName) on itself." }
        return interpreters.find { it.type == this }
            ?: error("Interpreter ${source.simpleName} declares $relation($simpleName) but $simpleName is not registered yet.")
    }

    /**
     * 解释器顺序约束
     */
    class InterpreterOrderSpec {

        internal val beforeTargets = linkedSetOf<Class<out ItemInterpreter>>()
        internal val afterTargets = linkedSetOf<Class<out ItemInterpreter>>()

        fun before(vararg types: Class<out ItemInterpreter>) {
            beforeTargets.addAll(types)
        }

        fun before(vararg types: KClass<out ItemInterpreter>) = before(*types.map { it.java }.toTypedArray())
        inline fun <reified T : ItemInterpreter> before() = before(T::class)

        fun after(vararg types: Class<out ItemInterpreter>) {
            afterTargets.addAll(types)
        }

        fun after(vararg types: KClass<out ItemInterpreter>) = after(*types.map { it.java }.toTypedArray())
        inline fun <reified T : ItemInterpreter> after() = after(T::class)
    }

    /**
     * 物品解释器集成
     */
    class InterpreterIntegrated<T : ItemInterpreter>(
        /** 物品解释器类型 **/
        val type: Class<out T>,
        /** 物品解释器构建器 **/
        val getter: Supplier<T>,
        /** 解释器层级顺序 **/
        val order: Int,
    )

    /**
     * 物品组件集成
     * @param serializer 序列化器
     */
    @Deprecated("被新组件系统替代")
    class ComponentIntegrated<T>(
        /** 物品组件类型 **/
        val type: Class<T>,
        /** 物品组件序列化器 **/
        val serializer: KSerializer<T>,
    ) {

        /**
         * 使用到的所有元素的所有节点名称
         */
        val elementNodes = serializer.descriptor.elementNodes

    }

}
