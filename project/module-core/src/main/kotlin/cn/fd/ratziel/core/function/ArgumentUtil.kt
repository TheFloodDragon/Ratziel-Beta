package cn.fd.ratziel.core.function

inline fun <reified T : Any> ArgumentContext.pop() = pop(T::class.java)

inline fun <reified T : Any> ArgumentContext.popOr(default: T) = popOr(T::class.java, default)

inline fun <reified T : Any> ArgumentContext.popOrNull() = popOrNull(T::class.java)