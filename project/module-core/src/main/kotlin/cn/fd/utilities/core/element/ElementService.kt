package cn.fd.utilities.core.element

/**
 * ElementService
 *
 * @author: TheFloodDragon
 * @since 2023/8/15 9:59
 */
object ElementService {

    /**
     * 元素注册表
     */
    private val registry: HashMap<ElementType, Set<ElementHandler>> = hashMapOf()

    /**
     * 注册元素类型
     * @param etype 元素类型
     * @param handlers 元素处理器
     */
    fun registerElementType(etype: ElementType, handlers: Set<ElementHandler>) {
        registry[etype] = handlers.toMutableSet()
    }

    fun registerElementType(etype: ElementType, handler: ElementHandler) {
        registry[etype] = mutableSetOf(handler)
    }

    fun registerElementType(etype: ElementType) {
        registry[etype] = mutableSetOf()
    }

    fun registerElementType(space: String, name: String, alias: Set<String>, handlers: Set<ElementHandler>) {
        registerElementType(ElementType(space, name, alias), handlers)
    }

    fun registerElementType(space: String, name: String, alias: Set<String>, handler: ElementHandler) {
        registerElementType(ElementType(space, name, alias), handler)
    }

    fun registerElementType(space: String, name: String, alias: Set<String>) {
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
    fun getHandlers(etype: ElementType): Set<ElementHandler> {
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
     * 获取元素注册表
     */
    fun getRegistry(): HashMap<ElementType, Set<ElementHandler>> {
        return registry
    }

}