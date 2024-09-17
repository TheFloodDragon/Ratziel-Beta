package cn.fd.ratziel.function

import cn.fd.ratziel.function.uncheck
import java.util.function.Function

inline fun <reified T : Any> ArgumentContext.pop() = pop(T::class.java)

inline fun <reified T : Any> ArgumentContext.popAll() = popAll(T::class.java)

inline fun <reified T : Any> ArgumentContext.popOr(default: T) = popOr(T::class.java, default)

inline fun <reified T : Any> ArgumentContext.popOrNull() = popOrNull(T::class.java)