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
    private val registry: HashMap<String, MutableSet<ElementType>> = hashMapOf()

    /**
     * 注册元素
     * @param id 元素标识符,可看作元素所在的命名空间
     * @param et 元素类型
     */
    fun registerElement(id: String, et: ElementType) {
        /**
         * 找得到: Add到Set
         * 找不到: Put到Map
         */
        registry[id].also { it?.add(et) } ?: et.also { registry[id] = mutableSetOf(it) }
    }

    fun registerElement(id: String, name: Array<String>, handlers: Array<ElementHandler>) {
        registerElement(id, ElementType(name, handlers))
    }

    fun unregisterElement(id: String) {
        registry.remove(id)
    }

    fun getRegistry(): HashMap<String, MutableSet<ElementType>> {
        return registry
    }

    fun getHandlers(id: String, name: String): Array<ElementHandler>? {
        return getInfo(id, name)?.handlers
    }

    fun getInfo(id: String, name: String): ElementType? {
        return getAllInfo(id)?.find { it.names.contains(name) }
    }

    fun getAllInfo(id: String): MutableSet<ElementType>? {
        return registry[id]
    }

}