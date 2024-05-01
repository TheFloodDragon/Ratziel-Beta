package cn.fd.ratziel.function.argument

import java.util.*
import java.util.concurrent.ArrayBlockingQueue

/**
 * DefaultArgumentQueue
 *
 * @author TheFloodDragon
 * @since 2024/5/1 13:55
 */
@Suppress("UNCHECKED_CAST")
class DefaultArgumentQueue(capacity: Int = 16) : ArgumentQueue {

    val arguments: Queue<Argument<*>> = ArrayBlockingQueue(capacity)

    override fun <T> pop(type: Class<T>) = arguments.find { it.type.isAssignableFrom(type) } as? Argument<out T> ?: throw ArgumentNotFoundException(type)

    override fun <T> popAll(type: Class<T>) = arguments.filter { it.type.isAssignableFrom(type) } as Iterable<Argument<out T>>

}