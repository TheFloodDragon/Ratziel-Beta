package cn.fd.ratziel.core.contextual

import java.util.function.Supplier

/**
 * AttachedContext - 附加的数据上下文
 *
 * @author TheFloodDragon
 * @since 2025/7/7 18:36
 */
interface AttachedContext {

    /**
     * 获取附加值
     */
    fun <T : Any> fetch(key: Any, ifAbsent: Supplier<T>): T

    /**
     * 获取附加值
     */
    fun <T : Any> fetchOrNull(key: Any): T?

    /**
     * 设置附加值
     */
    fun put(key: Any, value: Any)

    /**
     * 获取所有的上下文内容
     */
    val contents: Map<Any, Any>

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
         * 获取附加值
         */
        operator fun get(context: ArgumentContext): T

        /**
         * 设置附加值
         */
        operator fun set(context: ArgumentContext, value: T)

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