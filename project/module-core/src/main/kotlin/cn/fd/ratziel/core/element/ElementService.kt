package cn.fd.ratziel.core.element

import cn.fd.ratziel.core.element.api.ElementHandler

/**
 * ElementService
 * 负责元素类型和元素处理器的注册
 *
 * @author TheFloodDragon
 * @since 2023/8/15 9:59
 */
object ElementService {

    /**
     * 元素注册表
     */
    private val registry: HashMap<ElementType, List<ElementHandler>> = hashMapOf()

    /**
     * 注册元素类型
     * @param etype 元素类型
     * @param handlers 元素处理器
     */
    fun registerElementType(etype: ElementType, handlers: List<ElementHandler>) {
        registry[etype] = handlers
    }

    fun registerElementType(etype: ElementType, handler: ElementHandler) {
        registry[etype] = listOf(handler)
    }

    fun registerElementType(etype: ElementType) {
        registry[etype] = emptyList()
    }

    fun registerElementType(space: String, name: String, alias: Array<String>, handlers: List<ElementHandler>) {
        registerElementType(ElementType(space, name, alias), handlers)
    }

    fun registerElementType(space: String, name: String, alias: Array<String>, handler: ElementHandler) {
        registerElementType(ElementType(space, name, alias), handler)
    }

    fun registerElementType(space: String, name: String, alias: Array<String>) {
        registerElementType(ElementType(space, name, alias))
    }

    /**
     * 取消注册元素类型
     * @param etype 元素类型
     */
    fun unregisterElementType(etype: ElementType) {
        registry.remove(etype)
    }

    /**
     * 取消注册命名空间内的所有元素类型
     * @param space 命名空间名
     */
    fun unregisterSpace(space: String) {
        registry.filter {
            it.key.space == space // 命名空间匹配
        }.forEach { unregisterElementType(it.key) }
    }

    /**
     * 获取处理器
     */
    fun getHandlers(etype: ElementType): List<ElementHandler> {
        return registry[etype]!!
    }

    /**
     * 获取元素类型
     * @param space 命名空间
     * @param name 元素类型名称
     */
    fun getElementType(space: String, name: String): ElementType? {
        return getElementTypes(space).find { it.name == name }
    }

    /**
     * 获取命名空间下的所有元素类型
     * @param space 命名空间
     */
    fun getElementTypes(space: String): List<ElementType> {
        return registry.keys.filter { it.space == space }
    }

    /**
     * 获取所有注册的元素类型
     */
    fun getAllElementTypes(): Set<ElementType> {
        return registry.keys
    }

    /**
     * 获取元素注册表
     */
    fun getRegistry(): HashMap<ElementType, List<ElementHandler>> {
        return registry
    }

}