package cn.fd.ratziel.core.function

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.function.exception.ArgumentNotFoundException
import java.util.concurrent.CopyOnWriteArrayList

/**
 * SimpleArgumentContext
 *
 * @author TheFloodDragon
 * @since 2024/6/28 16:19
 */
open class SimpleArgumentContext(
    val list: MutableList<Any>
) : ArgumentContext {

    constructor(vararg values: Any) : this(CopyOnWriteArrayList<Any>().apply { addAll(values) })

    override fun <T> pop(type: Class<T>): T & Any {
        return popOrNull(type) ?: throw ArgumentNotFoundException(type);
    }

    override fun <T> popOr(type: Class<T>, def: T & Any): T & Any {
        return popOrNull(type) ?: def
    }

    override fun <T> popOrNull(type: Class<T>): T? {
        val find = list.find { type.isAssignableFrom(it::class.java) }
        @Suppress("UNCHECKED_CAST")
        return (find ?: return null) as T
    }

    override fun <T : Any?> popAll(type: Class<T>): List<T> {
        @Suppress("UNCHECKED_CAST")
        return list.filter { type.isAssignableFrom(it::class.java) } as List<T>
    }

    override fun add(element: Any) {
        list.add(element)
    }

    override fun remove(element: Any) {
        list.remove(element)
    }

    override fun removeAll(type: Class<*>) = list.forEachIndexed { index, element ->
        if (!type.isAssignableFrom(element::class.java)) list.removeAt(index)
    }

    override fun args(): Collection<Any> = list

}