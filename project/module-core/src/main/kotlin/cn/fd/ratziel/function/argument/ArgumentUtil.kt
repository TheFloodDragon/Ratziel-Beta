package cn.fd.ratziel.function.argument

inline fun <reified T> ArgumentFactory.pop() = pop(T::class.java)

inline fun <reified T> ArgumentFactory.popAll() = popAll(T::class.java)

inline fun <reified T> ArgumentFactory.popOr(value: T) = popOr(T::class.java, value)

inline fun <reified T> ArgumentFactory.popOrNull(): T? = popOrNull(T::class.java)