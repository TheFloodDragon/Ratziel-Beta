package cn.fd.ratziel.core.contextual

import java.util.concurrent.ConcurrentHashMap

/**
 * SimpleContext
 *
 * @author TheFloodDragon
 * @since 2024/6/28 16:19
 */
class SimpleContext(
    private val map: MutableMap<Class<*>, Any> = ConcurrentHashMap(),
) : ArgumentContext {

    constructor(vararg values: Any) : this() {
        for (value in values) this.map[value::class.java] = value
    }

    constructor(vararg values: Any, action: ArgumentContext.() -> Unit) : this(values) {
        action(this)
    }

    override fun <T> pop(type: Class<T>): T & Any {
        return popOrNull(type) ?: throw ArgumentNotFoundException(type)
    }

    override fun <T> popOrNull(type: Class<T>): T? {
        val find = map[type] // 寻找同类型
            ?: map.entries.find { type.isAssignableFrom(it.key) }?.value // 寻找子类
            ?: return null
        @Suppress("UNCHECKED_CAST")
        return find as T
    }

    override fun put(element: Any) {
        map[element::class.java] = element
    }

    override fun remove(type: Class<*>) {
        map.entries.removeIf { type.isAssignableFrom(it.key) }
    }

    override fun args(): Collection<Any> = map.values

}