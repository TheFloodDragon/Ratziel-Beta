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
    private val registry: HashMap<String, MutableSet<ElementInfo>> = hashMapOf()

    /**
     * 注册元素
     * @param id 元素标识符,可看作元素所在的命名空间
     * @param ei 元素信息
     */
    fun registerElement(id: String, ei: ElementInfo) {
        /**
         * 找得到: Add到Set
         * 找不到: Put到Map
         */
        registry[id].also { it?.add(ei) } ?: ei.also { registry[id] = mutableSetOf(it) }
    }

    fun registerElement(id: String, name: Array<String>, handlers: Array<ElementHandler>) {
        registerElement(id, ElementInfo(name, handlers))
    }

    fun unregisterElement(id: String) {
        registry.remove(id)
    }

    fun getRegistry(): HashMap<String, MutableSet<ElementInfo>> {
        return registry
    }

    fun getHandlers(id: String, name: String): Array<ElementHandler>? {
        return getInfo(id, name)?.handlers
    }

    fun getInfo(id: String, name: String): ElementInfo? {
        return getAllInfo(id)?.find { it.names.contains(name) }
    }

    fun getAllInfo(id: String): MutableSet<ElementInfo>? {
        return registry[id]
    }

}