package cn.fd.ratziel.core.functional

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

/**
 * SimpleAttachedContext
 *
 * @author TheFloodDragon
 * @since 2025/8/8 16:17
 */
class SimpleAttachedContext(val map: MutableMap<Any, Any> = ConcurrentHashMap()) : AttachedContext {

    /**
     * 获取附加值
     */
    override fun <T : Any> fetch(key: Any, ifAbsent: Supplier<T>): T {
        @Suppress("UNCHECKED_CAST")
        return map.computeIfAbsent(key) { ifAbsent.get() } as T
    }

    /**
     * 获取附加值
     */
    override fun <T : Any> fetchOrNull(key: Any): T? {
        @Suppress("UNCHECKED_CAST")
        return map[key] as? T
    }

    /**
     * 设置附加值
     */
    override fun put(key: Any, value: Any) {
        map[key] = value
    }

    override fun toString() = "AttachedContext$map"

    /**
     * [AttachedContext] 捕获器
     */
    class SimpleCatcher<T : Any>(val key: Any, val initializer: Supplier<T>) : AttachedContext.Catcher<T> {

        /**
         * 获取附加值
         */
        override operator fun get(attached: AttachedContext): T {
            return attached.fetch(key, initializer)
        }

        /**
         * 设置附加值
         */
        override operator fun set(attached: AttachedContext, value: T) {
            attached.put(key, value)
        }

        /**
         * 获取附加值
         */
        override operator fun get(context: ArgumentContext): T {
            return this[getAttached(context)]
        }

        /**
         * 设置附加值
         */
        override operator fun set(context: ArgumentContext, value: T) {
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
