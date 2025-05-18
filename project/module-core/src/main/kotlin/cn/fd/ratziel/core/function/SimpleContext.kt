package cn.fd.ratziel.core.function

import cn.fd.ratziel.core.exception.ArgumentNotFoundException

/**
 * SimpleContext
 *
 * @author TheFloodDragon
 * @since 2024/6/28 16:19
 */
class SimpleContext(
    private val map: HierarchicalMap = HierarchicalMap(1),
) : ArgumentContext {

    constructor(vararg values: Any) : this() {
        for (value in values) this.map.put(value)
    }

    override fun <T> pop(type: Class<T>): T & Any {
        return popOrNull(type) ?: throw ArgumentNotFoundException(type)
    }

    override fun <T> popOr(type: Class<T>, def: T & Any): T & Any {
        return popOrNull(type) ?: def
    }

    override fun <T> popOrNull(type: Class<T>): T? {
        val find = map.get(type) ?: return null
        @Suppress("UNCHECKED_CAST")
        return find as T
    }

    override fun put(element: Any) {
        map.put(element)
    }

    override fun remove(element: Any) {
        if (element is Class<*>) {
            map.remove(element)
        } else {
            map.remove(element::class.java)
        }
    }

    override fun args(): Collection<Any> = map.values()

}