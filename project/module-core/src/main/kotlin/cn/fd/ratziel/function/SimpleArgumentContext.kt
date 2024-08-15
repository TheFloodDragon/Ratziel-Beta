package cn.fd.ratziel.function

import java.util.concurrent.CopyOnWriteArrayList

/**
 * SimpleArgumentContext
 *
 * @author TheFloodDragon
 * @since 2024/6/28 16:19
 */
open class SimpleArgumentContext(
    val list: MutableList<Any>
) : ArgumentContext, MutableList<Any> by list {

    constructor(vararg values: Any) : this(CopyOnWriteArrayList<Any>().apply { addAll(values) })

    override fun <T : Any> popOrNull(type: Class<T>): T? {
        return uncheck(list.find { type.isAssignableFrom(it::class.java) })
    }

    override fun <T : Any> popAll(type: Class<T>): Iterable<T> {
        return uncheck(list.filter { type.isAssignableFrom(it::class.java) })
    }

    override fun args(): Collection<Any> = list

}