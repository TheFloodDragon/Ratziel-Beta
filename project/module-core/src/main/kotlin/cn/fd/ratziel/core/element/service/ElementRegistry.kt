package cn.fd.ratziel.core.element.service

import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.element.ElementType
import cn.fd.ratziel.core.util.sortPriority
import java.util.concurrent.ConcurrentHashMap

/**
 * ElementRegistry
 * 管理元素类型的注册
 *
 * @author TheFloodDragon
 * @since 2023/8/15 9:59
 */
object ElementRegistry {

    /**
     * 元素注册表
     */
    @JvmStatic
    val registry: MutableMap<ElementType, ElementHandlerGroup> = ConcurrentHashMap()

    /**
     * 注册元素类型
     * @param type 元素类型
     * @param handler 元素处理器
     * @param priority 处理器优先级
     */
    @JvmStatic
    fun register(type: ElementType, handler: ElementHandler, priority: Byte = 0) = register(type).register(handler, priority)

    @JvmStatic
    fun register(type: ElementType) = registry.computeIfAbsent(type) { _ -> ElementHandlerGroup() }

    @JvmStatic
    fun register(
        space: String,
        name: String,
        alias: Array<String>,
        handler: ElementHandler,
        priority: Byte = 0,
    ) = register(ElementType(space, name, alias), handler, priority)

    /**
     * 取消注册元素类型
     * @param type 元素类型
     */
    @JvmStatic
    fun unregister(type: ElementType) =
        registry.remove(type)

    /**
     * 取消注册元素类型处理器
     */
    @JvmStatic
    fun unregister(type: ElementType, handler: ElementHandler) =
        registry[type]?.unregister(handler)

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
    fun getHandlerGroup(type: ElementType): ElementHandlerGroup? = registry[type]

    /**
     * 获取处理器
     * @param type 元素类型
     */
    @JvmStatic
    fun getHandlersByType(type: ElementType): List<ElementHandler> {
        val handlers = registry[type]?.handlers ?: return emptyList()
        return handlers.sortPriority()
    }

    @JvmStatic
    fun getAllHandlers(): List<ElementHandler> {
        val handlers = registry.values.flatMap { it.handlers }
        return handlers.sortPriority()
    }

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