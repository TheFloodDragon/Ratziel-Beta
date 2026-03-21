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
interface AttachedContext {

    /**
     * 获取附加值（通过 Catcher）
     * 若传入初始化函数返回 null，则回退到 Catcher 自身的初始化函数。
     */
    fun <T : Any> fetch(catcher: Catcher<T>, ifAbsent: () -> T?): T

    /**
     * 设置附加值（通过 Catcher）
     */
    operator fun <T : Any> set(catcher: Catcher<T>, value: T)

    /**
     * 获取附加值（通过 Catcher）
     */
    operator fun <T : Any> get(catcher: Catcher<T>): T

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
        operator fun invoke(attached: AttachedContext, block: (T) -> T): T

        /**
         * 获取附加值
         */
        operator fun get(context: ArgumentContext): T

        /**
         * 设置附加值
         */
        operator fun set(context: ArgumentContext, value: T)

        /**
         * 更新附加值
         */
        operator fun invoke(context: ArgumentContext, block: (T) -> T): T

        /**
         * 从 [ArgumentContext] 中获取附加的上下文
         */
        fun attach(context: ArgumentContext): AttachedContext

    }

    class PropertyCatcherDelegate<T : Any>(initializer: () -> T) : ReadOnlyProperty<Any?, Catcher<T>> {
        val catcher = catcherOf(initializer)
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
         * 提供默认初始化，取值时若不存在则自动初始化。
         */
        @JvmStatic
        fun <T : Any> catcherOf(initializer: () -> T): Catcher<T> = AttachedContextImpl.CatcherImpl(initializer)
 
        /**
         * 创建一个新的 [AttachedContext.Catcher]
         * 用于属性委托：`val x by AttachedContext.catcher { ... }`
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
private class AttachedContextImpl(
    val map: ConcurrentHashMap<AttachedContext.Catcher<*>, Any> = ConcurrentHashMap()
) : AttachedContext {

    /**
     * 获取附加值
     */
    override fun <T : Any> fetch(catcher: AttachedContext.Catcher<T>, ifAbsent: () -> T?): T {
        @Suppress("UNCHECKED_CAST")
        map[catcher]?.let { return it as T }

        val resolved = ifAbsent() ?: when (catcher) {
            is CatcherImpl<T> -> catcher.initializer()
            else -> catcher[this]
        }

        @Suppress("UNCHECKED_CAST")
        return (map.putIfAbsent(catcher, resolved) ?: resolved) as T
    }

    override operator fun <T : Any> get(catcher: AttachedContext.Catcher<T>): T = catcher[this]

    override operator fun <T : Any> set(catcher: AttachedContext.Catcher<T>, value: T) {
        map[catcher] = value
    }

    override fun toString() = "AttachedContext$map"

    /**
     * [AttachedContext] 捕获器
     */
    open class CatcherImpl<T : Any>(
        val initializer: () -> T
    ) : AttachedContext.Catcher<T> {

        /**
         * 获取附加值
         */
        override operator fun get(attached: AttachedContext): T {
            return attached.fetch(this, initializer)
        }

        /**
         * 设置附加值
         */
        override operator fun set(attached: AttachedContext, value: T) {
            attached[this] = value
        }

        /**
         * 更新附加值
         */
        override operator fun invoke(attached: AttachedContext, block: (T) -> T): T {
            val updated = block(get(attached))
            set(attached, updated)
            return updated
        }

        /**
         * 获取附加值
         */
        override operator fun get(context: ArgumentContext): T = this[attach(context)]

        /**
         * 设置附加值
         */
        override operator fun set(context: ArgumentContext, value: T) = this.set(attach(context), value)

        /**
         * 更新附加值
         */
        override operator fun invoke(context: ArgumentContext, block: (T) -> T): T = this.invoke(attach(context), block)

        /**
         * 获取 [AttachedContext] (没有的话就创建)
         */
        override fun attach(context: ArgumentContext): AttachedContext {
            return context.popOrPut(AttachedContext::class.java) { AttachedContextImpl() }
        }
    }


}
