package cn.fd.ratziel.core.contextual

import java.util.function.Supplier

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
    fun <T : Any> fetch(key: Any, ifAbsent: Supplier<T>): T

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
        operator fun get(context: ArgumentContext): T = this.get(attach(context))

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
        fun <T : Any> catcher(key: Any, initializer: Supplier<T>): Catcher<T> = AttachedContextImpl.CatcherImpl(key, initializer)

    }

}