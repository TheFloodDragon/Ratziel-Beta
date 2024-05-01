package cn.fd.ratziel.function.argument

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * ArgumentQueue
 *
 * @author TheFloodDragon
 * @since 2024/5/1 13:55
 */
@Suppress("UNCHECKED_CAST")
open class DefaultArgumentFactory(open val queue: Queue<Argument<*>>) : ArgumentFactory, Queue<Argument<*>> by queue {

    constructor(collection: Collection<Argument<*>>) : this(ConcurrentLinkedQueue(collection))

    constructor(vararg argument: Argument<*>) : this(argument.toList())

    override fun <T> pop(type: Class<T>) =
        queue.find { it.type.isAssignableFrom(type) }?.value as? T
            ?: throw ArgumentNotFoundException(type)

    override fun <T> popOr(type: Class<T>, default: T): T =
        try {
            pop(type)
        } catch (ex: ArgumentNotFoundException) {
            default
        }

    override fun <T> popOrNull(type: Class<T>): T? = try {
        pop(type)
    } catch (ex: ArgumentNotFoundException) {
        null
    }

    override fun <T> popAll(type: Class<T>) =
        queue.filter { it.type.isAssignableFrom(type) } as Iterable<T>

    override fun addArg(argument: Argument<*>) = queue.add(argument)

    override fun removeArg(argument: Argument<*>) = queue.remove(argument)

}