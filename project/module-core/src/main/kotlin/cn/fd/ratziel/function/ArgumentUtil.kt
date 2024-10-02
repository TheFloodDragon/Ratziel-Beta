package cn.fd.ratziel.function

inline fun <reified T : Any> ArgumentContext.get() = get(T::class.java)

inline fun <reified T : Any> ArgumentContext.getOr(default: T) = getOr(T::class.java, default)

inline fun <reified T : Any> ArgumentContext.getOrNull() = getOrNull(T::class.java)