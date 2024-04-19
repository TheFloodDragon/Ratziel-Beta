package cn.fd.ratziel.core.function

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * 简化多异步任务的过程
 */
inline fun <T> FutureFactory(block: FutureFactory<T>.() -> Unit) = FutureFactory<T>().apply(block)

/**
 * FutureFactory - 用于管控多 [CompletableFuture] 的任务
 *
 * @author TheFloodDragon
 * @since 2023/11/19 11:27
 */
open class FutureFactory<T> {

    /**
     * 提交的任务列表
     */
    protected open val tasks: ConcurrentLinkedQueue<CompletableFuture<T>> = ConcurrentLinkedQueue()

    /**
     * 清空所有任务
     */
    open fun clear() = tasks.clear()

    /**
     * 提交任务
     */
    open fun submit(task: CompletableFuture<T>) = task.also { tasks += it }

    /**
     * 提交任务 (通过列表的映射)
     */
    open fun <E> submitWith(list: Iterable<E>, action: E.() -> T) = submit(CompletableFuture())

    /**
     * 异步执行任务并提交
     */
    open fun supplyAsync(function: Supplier<T>) = submit(CompletableFuture.supplyAsync(function))

    /**
     * 当所有任务完成时 (非阻塞)
     * 并提供所有任务返回值的列表
     * @param action 对返回值进行的操作
     */
    open fun whenAllComplete(action: Consumer<List<T>>): CompletableFuture<List<T>> = tasks.toTypedArray().let { futures ->
        CompletableFuture.allOf(*futures).thenApply {
            futures.map { it.get() }.also { action.accept(it) }
        }
    }

    /**
     * 等待所有任务完成 (阻塞)
     */
    open fun waitAll() = CompletableFuture.allOf(*tasks.toTypedArray()).join()

}