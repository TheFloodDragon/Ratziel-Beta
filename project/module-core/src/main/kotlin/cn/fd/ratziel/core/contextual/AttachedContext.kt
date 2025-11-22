package cn.fd.ratziel.core.contextual

import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * AttachedContext - 附加的数据上下文
 *
 * @author TheFloodDragon
 * @since 2025/7/7 18:36
 */
interface AttachedContext : MutableMap<Any, Any> {

    /**
     * 获取附加值
     */
    fun <T : Any> fetch(key: Any, ifAbsent: () -> T): T

    /**
     * 获取附加值
     */
    fun <T : Any> fetchOrNull(key: Any): T?

    /**
     * [AttachedContext] 捕获器
     */
    interface Catcher<T : Any> {

        /**
         * 获取附加值
         */
        operator fun get(attached: AttachedContext): T

        /**
         * 设置附加值
         */
        operator fun set(attached: AttachedContext, value: T)

        /**
         * 更新附加值
         */
        operator fun invoke(attached: AttachedContext, block: (T) -> T): T {
            val updated = block(get(attached))
            set(attached, updated)
            return updated
        }

        /**
         * 获取附加值
         */
        operator fun get(context: ArgumentContext): T = this[attach(context)]

        /**
         * 设置附加值
         */
        operator fun set(context: ArgumentContext, value: T) = this.set(attach(context), value)

        /**
         * 更新附加值
         */
        operator fun invoke(context: ArgumentContext, block: (T) -> T) = this.invoke(attach(context), block)

        /**
         * 从 [ArgumentContext] 中获取附加的上下文
         */
        fun attach(context: ArgumentContext): AttachedContext

    }

    class PropertyCatcherDelegate<T : Any>(initializer: () -> T) : ReadOnlyProperty<Any?, Catcher<T>> {
        val catcher = catcher(this, initializer)
        override operator fun getValue(thisRef: Any?, property: KProperty<*>) = this.catcher
    }

    companion object {

        /**
         * 创建一个新的 [AttachedContext]
         */
        @JvmStatic
        fun newContext(): AttachedContext = AttachedContextImpl()

        /**
         * 创建一个新的 [AttachedContext.Catcher]
         */
        @JvmStatic
        fun <T : Any> catcher(key: Any, initializer: () -> T): Catcher<T> = AttachedContextImpl.CatcherImpl(key, initializer)

        /**
         * 创建一个新的 [AttachedContext.Catcher]
         */
        @JvmStatic
        fun <T : Any> catcher(initializer: () -> T) = PropertyCatcherDelegate(initializer)

    }

}

/**
 * AttachedContextImpl
 *
 * @author TheFloodDragon
 * @since 2025/8/8 16:17
 */
private class AttachedContextImpl(val map: MutableMap<Any, Any> = ConcurrentHashMap()) : AttachedContext, MutableMap<Any, Any> by map {

    /**
     * 获取附加值
     */
    override fun <T : Any> fetch(key: Any, ifAbsent: () -> T): T {
        @Suppress("UNCHECKED_CAST")
        return map.computeIfAbsent(key) { ifAbsent() } as? T
            ?: ifAbsent().also { map[key] = it } // 强行修正
    }

    /**
     * 获取附加值
     */
    override fun <T : Any> fetchOrNull(key: Any): T? {
        @Suppress("UNCHECKED_CAST")
        return map[key] as? T
    }

    override fun toString() = "AttachedContext$map"

    /**
     * [AttachedContext] 捕获器
     */
    class CatcherImpl<T : Any>(val key: Any, val initializer: () -> T) : AttachedContext.Catcher<T> {

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
            attached[key] = value
        }

        /**
         * 获取 [AttachedContext] (没有的话就创建)
         */
        override fun attach(context: ArgumentContext): AttachedContext {
            return context.popOrPut(AttachedContext::class.java) { AttachedContextImpl() }
        }

    }

}
