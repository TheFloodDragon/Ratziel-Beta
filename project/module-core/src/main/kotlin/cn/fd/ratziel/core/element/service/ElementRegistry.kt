package cn.fd.ratziel.core.element.service

import cn.fd.ratziel.core.element.ElementType
import cn.fd.ratziel.core.element.api.ElementHandler
import java.util.function.Consumer

/**
 * ElementRegistry
 * 管理元素类型的注册
 *
 * @author TheFloodDragon
 * @since 2023/8/15 9:59
 */
object ElementRegistry {

    /**
     * 默认元素处理器优先级
     */
    const val DEFAULT_PRIORITY: Byte = 0

    /**
     * 元素注册表
     */
    @JvmStatic
    val registry: HashMap<ElementType, ElementHandlerGroup> = hashMapOf()

    /**
     * 注册元素类型
     * @param etype 元素类型
     * @param handler 元素处理器
     * @param priority 处理器优先级
     */
    @JvmStatic
    fun register(etype: ElementType, handler: ElementHandler, priority: Byte = DEFAULT_PRIORITY) =
        register(etype).register(handler, priority)

    @JvmStatic
    fun register(etype: ElementType) =
        registry.computeIfAbsent(etype) { ElementHandlerGroup() }

    @JvmStatic
    fun register(
        space: String,
        name: String,
        alias: Array<String>,
        handler: ElementHandler,
        priority: Byte = DEFAULT_PRIORITY,
    ) {
        register(ElementType(space, name, alias), handler, priority)
    }

    /**
     * 取消注册元素类型
     * @param etype 元素类型
     */
    @JvmStatic
    fun unregister(etype: ElementType) =
        registry.remove(etype)

    /**
     * 取消注册元素类型处理器
     */
    @JvmStatic
    fun unregister(etype: ElementType, handler: ElementHandler) =
        registry[etype]?.unregister(handler)

    /**
     * 取消注册命名空间内的所有元素类型
     * @param space 命名空间名
     */
    @JvmStatic
    fun unregisterSpace(space: String) {
        registry.filter {
            it.key.space == space // 命名空间匹配
        }.forEach { unregister(it.key) }
    }

    /**
     * 获取处理器组
     */
    @JvmStatic
    fun getHandlerGroup(etype: ElementType): ElementHandlerGroup? = registry[etype]

    /**
     * 获取处理器表
     */
    @JvmStatic
    fun getHandlerMap(etype: ElementType) = getHandlerGroup(etype)?.handlerMap

    /**
     * 根据优先级提供处理器以供操作
     */
    @JvmStatic
    fun runWithHandlers(etype: ElementType, function: Consumer<Pair<Byte, ElementHandler>>) =
        getHandlerMap(etype)?.forEach { (priority, handlers) ->
            handlers.forEach {
                function.accept(Pair(priority, it))
            }
        }

    /**
     * 获取处理器
     * @param etype 元素类型
     * @param priority 处理器优先级
     */
    @JvmStatic
    fun getHandlers(etype: ElementType, priority: Byte): List<ElementHandler> =
        getHandlerMap(etype)?.get(priority) ?: emptyList()

    @JvmStatic
    fun getHandlers(etype: ElementType): List<ElementHandler> =
        getHandlerMap(etype)?.flatMap { it.value } ?: emptyList()

    @JvmStatic
    fun getHandlers(): List<ElementHandler> = registry.values.flatMap { it.handlerMap.values.flatten() }

    /**
     * 获取元素类型
     * @param space 命名空间
     * @param name 元素类型名称
     */
    @JvmStatic
    fun getElementType(space: String, name: String): ElementType? {
        return getElementTypes(space).find { it.name == name }
    }

    /**
     * 获取命名空间下的所有元素类型
     * @param space 命名空间
     */
    @JvmStatic
    fun getElementTypes(space: String): List<ElementType> {
        return registry.keys.filter { it.space == space }
    }

    /**
     * 获取所有注册的元素类型
     */
    @JvmStatic
    fun getAllElementTypes(): Set<ElementType> {
        return registry.keys
    }

}