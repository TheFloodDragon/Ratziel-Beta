package cn.fd.ratziel.common.element.registry

import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.element.ElementType
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
    val registry: MutableMap<ElementType, ElementHandler> = ConcurrentHashMap()

    /**
     * 获取元素处理器
     */
    operator fun get(key: ElementType): ElementHandler {
        return registry[key] ?: throw IllegalStateException("Element type ${key.name} not registered.")
    }

    /**
     * 寻找元素处理器对应的 元素类型 [ElementType]
     */
    fun findType(clazz: Class<out ElementHandler>): ElementType {
        return registry.entries.find { it.value::class.java == clazz }?.key ?: throw IllegalStateException("Cannot find element type for '$clazz'.")
    }

    /**
     * 注册元素类型
     * @param type 元素类型
     * @param handler 元素处理器
     */
    @JvmStatic
    fun register(type: ElementType, handler: ElementHandler) {
        registry[type] = handler
    }

    @JvmStatic
    fun register(
        space: String,
        name: String,
        alias: Array<String>,
        handler: ElementHandler,
    ) = register(ElementType(space, name, alias), handler)

    @JvmStatic
    fun unregister(type: ElementType) {
        registry.remove(type)
    }

}