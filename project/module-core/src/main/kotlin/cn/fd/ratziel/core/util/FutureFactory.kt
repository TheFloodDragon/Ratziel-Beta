package cn.fd.ratziel.core.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.function.Consumer
import java.util.function.Function
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
    protected open val tasks: ConcurrentLinkedQueue<CompletableFuture<T?>> = ConcurrentLinkedQueue()

    /**
     * 清空所有任务
     */
    open fun clear() = tasks.clear()

    /**
     * 提交任务
     */
    open fun submitFuture(task: CompletableFuture<T?>) = task.also { tasks += it }

    open fun CompletableFuture<T?>.submit() = submitFuture(this)

    /**
     * 创建异步任务但不提交
     */
    open fun supplyAsync(function: Supplier<T?>) = CompletableFuture.supplyAsync(function)

    open fun supplyAsync(executor: Executor, function: Supplier<T?>) = CompletableFuture.supplyAsync(function, executor)

    /**
     * 当所有任务完成时 (非阻塞)
     * 并提供所有任务返回值的列表
     * @param action 对返回值进行的操作
     */
    open fun <R> whenComplete(action: Function<List<T>, R>): CompletableFuture<R> = tasks.toTypedArray().let { futures ->
        CompletableFuture.allOf(*futures).thenApply {
            futures.mapNotNull { it.get() }.let { action.apply(it) }
        }
    }

    open fun whenComplete(action: Consumer<List<T>> = Consumer {}): CompletableFuture<List<T>> = whenComplete(Function { action.accept(it); it })

    /**
     * 等待所有任务完成 (阻塞)
     */
    open fun waitAll() = CompletableFuture.allOf(*tasks.toTypedArray()).join()

}