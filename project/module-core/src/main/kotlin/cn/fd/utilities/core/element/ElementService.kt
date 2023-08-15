package cn.fd.utilities.core.element

import cn.fd.utilities.core.element.parser.ElementHandler

/**
 * ElementService
 *
 * @author: TheFloodDragon
 * @since 2023/8/15 9:59
 */
object ElementService {

    private val registry: HashMap<Array<String>, Array<ElementHandler>> = hashMapOf()

    fun getRegistry(): Map<Array<String>, Array<ElementHandler>> {
        return registry
    }

    fun registerElement(et: ElementType) {
        registry[et.name] = et.handlers
    }

    fun unregisterElement(at: Array<String>) {
        registry.remove(at)
    }

}