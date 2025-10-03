package cn.fd.ratziel.core.contextual

inline fun <reified T : Any> ArgumentContext.pop() = pop(T::class.java)

inline fun <reified T : Any> ArgumentContext.popOr(noinline def: () -> T) = popOr(T::class.java, def)

inline fun <reified T : Any> ArgumentContext.popOrNull() = popOrNull(T::class.java)

operator fun ArgumentContext.plus(element: Any) = this.copy().apply { put(element) }
