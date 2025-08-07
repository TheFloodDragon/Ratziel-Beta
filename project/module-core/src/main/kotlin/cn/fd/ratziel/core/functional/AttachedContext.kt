package cn.fd.ratziel.core.functional

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

/**
 * AttachedContext - 附加的数据上下文
 *
 * @author TheFloodDragon
 * @since 2025/7/7 18:36
 */
class AttachedContext(val map: MutableMap<Any, Any> = ConcurrentHashMap()) {

    /**
     * 获取缓存
     */
    fun <T : Any> fetch(key: Any, ifAbsent: Supplier<T>): T {
        @Suppress("UNCHECKED_CAST")
        return map.computeIfAbsent(key) { ifAbsent.get() } as T
    }

    /**
     * 获取缓存
     */
    fun <T> fetchOrNull(key: Any): T? {
        @Suppress("UNCHECKED_CAST")
        return map[key] as? T
    }

    /**
     * 设置缓存
     */
    fun put(key: Any, value: Any) {
        map[key] = value
    }

    /**
     * 作为 [ArgumentContextProvider]
     */
    fun asContextProvider() = ArgumentContextProvider { SimpleContext(this) }

    override fun toString() = "AttachedContext$map"

    /**
     * [AttachedContext] 捕获器
     */
    class Catcher<T : Any>(val key: Any, val initializer: Supplier<T>) {

        /**
         * 获取
         */
        operator fun get(attached: AttachedContext): T {
            return attached.fetch(key, initializer)
        }

        /**
         * 设置
         */
        operator fun set(attached: AttachedContext, value: T) {
            attached.put(key, value)
        }

        /**
         * 获取
         */
        operator fun get(context: ArgumentContext): T {
            return this[getAttached(context)]
        }

        /**
         * 设置
         */
        operator fun set(context: ArgumentContext, value: T) {
            this[getAttached(context)] = value
        }

        /**
         * 获取 [AttachedContext] (没有的话就创建)
         */
        private fun getAttached(context: ArgumentContext): AttachedContext {
            return context.popOr(AttachedContext::class.java) {
                error("Cannot catch AttachedContext from context $context")
            }
        }

    }

}
