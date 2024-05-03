package cn.fd.ratziel.function.argument

import cn.fd.ratziel.function.argument.exception.ArgumentNotFoundException
import cn.fd.ratziel.function.util.uncheck
import java.util.function.Function

inline fun <reified T : Any> ArgumentFactory.pop() = pop(T::class.java)

inline fun <reified T : Any> ArgumentFactory.popAll() = popAll(T::class.java)

inline fun <reified T : Any> ArgumentFactory.popOrNull(): Argument<T>? =
    try {
        pop(T::class.java)
    } catch (ex: ArgumentNotFoundException) {
        null
    }

inline fun <reified T : Any> ArgumentFactory.popOr(default: T): Argument<T> =
    try {
        pop(T::class.java)
    } catch (ex: ArgumentNotFoundException) {
        SingleArgument(default)
    }

/**
 * 判断参数是否为目标类型
 * 相当于 V is T
 */
infix fun <T> Argument<*>.instanceof(type: Class<T>): Boolean = type.isAssignableFrom(this.type)

inline fun <reified T> Argument<*>.instanceof(): Boolean = this.instanceof(T::class.java)

/**
 * 如果参数为目标类型, 则执行 [onTrue], 反之执行 [onFalse]
 * 相当 if.. else...
 */
fun <T : Any, R, V : Any> Argument<V>.ascertain(
    clazz: Class<T>,
    onTrue: Function<Argument<T>, R>,
    onFalse: Function<Argument<V>, R>
): R = if (this.instanceof(clazz)) onTrue.apply(uncheck(this)) else onFalse.apply(this)

inline fun <reified T : Any, R, V : Any> Argument<V>.ascertain(
    onTrue: Function<Argument<T>, R>,
    onFalse: Function<Argument<V>, R>
): R = this.ascertain(T::class.java, onTrue, onFalse)