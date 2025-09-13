package cn.fd.ratziel.core.element

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

/**
 * ElementHandler - 元素处理器
 *
 * @author TheFloodDragon
 * @since 2024/4/21 9:46
 */
interface ElementHandler {

    /**
     * 处理元素集
     */
    suspend fun handle(elements: Collection<Element>)

    /**
     * 处理并更新单个元素
     */
    suspend fun update(element: Element)

    /**
     * ParallelHandler - 并行处理元素的处理器
     *
     * @author TheFloodDragon
     * @since 2024/4/21 9:46
     */
    interface ParralHandler : ElementHandler {

        /**
         * 处理单个元素
         */
        fun handle(element: Element)

        /**
         * 在元素加载前调用
         *
         * @param elements 交给该 [ElementHandler] 解析的所有元素
         */
        fun onStart(elements: Collection<Element>) = Unit

        /**
         * 在元素加载后调用
         */
        fun onEnd() = Unit

        override suspend fun handle(elements: Collection<Element>) {
            // 开始处理
            onStart(elements)
            // 处理每个元素
            coroutineScope {
                elements.map {
                    launch {
                        try {
                            this@ParralHandler.handle(it)
                        } catch (ex: Throwable) {
                            // 失败触发回调
                            fail(it, ex)
                        }
                    }
                }.joinAll()
            }
            // 结束处理
            onEnd()
        }

        override suspend fun update(element: Element) = this.handle(element)

    }

    companion object {

        /**
         * 失败时的回调函数
         */
        var failureCallback: ((Element, Throwable?) -> Unit)? = null

        /**
         * 标记某个元素为处理失败
         */
        fun fail(element: Element, cause: Throwable? = null) {
            failureCallback?.invoke(element, cause)
            cause?.printStackTrace()
        }

    }

}