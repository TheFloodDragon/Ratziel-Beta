package cn.fd.ratziel.function.argument

import cn.fd.ratziel.function.argument.exception.ArgumentNotFoundException

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