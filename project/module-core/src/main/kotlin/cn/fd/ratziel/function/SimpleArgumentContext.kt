package cn.fd.ratziel.function

import cn.fd.ratziel.function.exception.ArgumentNotFoundException
import java.util.concurrent.ConcurrentHashMap

/**
 * SimpleArgumentContext
 *
 * @author TheFloodDragon
 * @since 2024/6/28 16:19
 */
open class SimpleArgumentContext(
    val map: MutableMap<Class<*>, Any>
) : ArgumentContext {

    constructor(vararg values: Any) : this(ConcurrentHashMap<Class<*>, Any>()) {
        values.forEach { map[it::class.java] = it }
    }

    override fun <T> get(type: Class<T>): T & Any {
        return getOrNull(type) ?: throw ArgumentNotFoundException(type);
    }

    override fun <T> getOr(type: Class<T>, def: T & Any): T & Any {
        return getOrNull(type) ?: def
    }

    override fun <T> getOrNull(type: Class<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return map[type] as? T
    }

    override fun put(element: Any) {
        map[element::class.java] = element
    }

    override fun remove(element: Any) {
        map.remove(element::class.java)
    }

    override fun args(): Collection<Any> = map.values

}