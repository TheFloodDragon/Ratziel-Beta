package cn.fd.ratziel.core.function

import java.util.function.Supplier

inline fun <reified T : Any> ArgumentContext.pop() = pop(T::class.java)

inline fun <reified T : Any> ArgumentContext.popOr(default: Supplier<T>) = popOr(T::class.java, default)

inline fun <reified T : Any> ArgumentContext.popOrNull() = popOrNull(T::class.java)