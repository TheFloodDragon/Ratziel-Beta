package cn.fd.ratziel.core.functional

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

/**
 * CacheContext
 *
 * @author TheFloodDragon
 * @since 2025/7/7 18:36
 */
class CacheContext(val cache: MutableMap<Any, Any> = ConcurrentHashMap()) {

    /**
     * 获取缓存
     */
    fun <T : Any> fetch(key: Any, ifAbsent: Supplier<T>): T {
        @Suppress("UNCHECKED_CAST")
        return cache.computeIfAbsent(key) { ifAbsent.get() } as T
    }

    /**
     * 获取缓存
     */
    fun <T> fetchOrNull(key: Any): T? {
        @Suppress("UNCHECKED_CAST")
        return cache[key] as? T
    }

    /**
     * 设置缓存
     */
    fun put(key: Any, value: Any) {
        cache[key] = value
    }

    /**
     * 缓存捕获器 - 用于确定缓存类型
     */
    class Catcher<T : Any>(val key: Any, val initializer: Supplier<T>) {

        /**
         * 捕获缓存
         */
        fun catch(context: ArgumentContext): T {
            return context.cacheContext().fetch(key, initializer)
        }

        /**
         * 设置缓存
         */
        fun setCache(context: ArgumentContext, value: T) {
            context.cacheContext().put(key, value)
        }

        private fun ArgumentContext.cacheContext() =
            this.popOr(CacheContext::class.java) {
                CacheContext().also { put(it) }
            }

    }

}
