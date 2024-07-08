package cn.fd.ratziel.function.argument

import cn.fd.ratziel.function.argument.exception.ArgumentNotFoundException
import cn.fd.ratziel.function.util.uncheck
import java.util.concurrent.CopyOnWriteArraySet

/**
 * SimpleArgumentContext
 *
 * @author TheFloodDragon
 * @since 2024/6/28 16:19
 */
open class SimpleArgumentContext(
    val collection: MutableCollection<Any>
) : ArgumentContext, MutableCollection<Any> by collection {

    constructor(vararg values: Any) : this(CopyOnWriteArraySet<Any>().apply { values.forEach { add(it) } })

    override fun <T> popOrNull(type: Class<T>): T? {
        return uncheck(collection.find { type.isAssignableFrom(it::class.java) })
    }

    override fun <T> popAll(type: Class<T>): Iterable<T> {
        return uncheck(collection.filter { type.isAssignableFrom(it::class.java) })
    }

    override fun args(): Collection<Any> {
        return collection
    }

    override fun <T> pop(type: Class<T>): T {
        return popOrNull(type) ?: throw ArgumentNotFoundException(type)
    }

    override fun <T> popOr(type: Class<T>, default: T): T {
        return popOrNull(type) ?: default
    }

}