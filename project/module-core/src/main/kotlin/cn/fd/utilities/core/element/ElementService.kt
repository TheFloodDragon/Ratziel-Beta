package cn.fd.utilities.core.element

import cn.fd.utilities.core.element.parser.ElementHandler

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
    private val registry: HashMap<String, ElementInfo> = hashMapOf()

    /**
     * 注册元素
     * @param id 元素标识符,可看作元素所在的命名空间
     * @param ei 元素信息
     */
    fun registerElement(id: String, ei: ElementInfo) {
        registry[id] = ei
    }

    fun registerElement(id: String, name: Array<String>, handlers: Array<ElementHandler>) {
        registerElement(id, ElementInfo(name, handlers))
    }

    fun unregisterElement(id: String) {
        registry.remove(id)
    }

    fun getRegistry(): HashMap<String, ElementInfo> {
        return registry
    }

    fun getElementHandlers(id: String): Array<ElementHandler>? {
        return getElementInfo(id)?.handlers
    }

    fun getElementInfo(id: String): ElementInfo? {
        return registry[id]
    }

}