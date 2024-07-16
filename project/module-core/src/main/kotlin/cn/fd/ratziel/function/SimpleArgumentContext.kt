package cn.fd.ratziel.function

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

    override fun <T : Any> popOrNull(type: Class<T>): T? {
        return uncheck(collection.find { type.isAssignableFrom(it::class.java) })
    }

    override fun <T : Any> popAll(type: Class<T>): Iterable<T> {
        return uncheck(collection.filter { type.isAssignableFrom(it::class.java) })
    }

    override fun args(): Collection<Any> {
        return collection
    }

}