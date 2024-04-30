package cn.fd.ratziel.core.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

/**
 * 捕获异常并打印
 */
inline fun <T> CompletableFuture<T>.printOnException(): CompletableFuture<T> = this.exceptionally { it.printStackTrace();null }

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
open class FutureFactory<T>(
    /**
     * 任务列表
     */
    protected open val tasks: MutableCollection<CompletableFuture<T>>
) : MutableCollection<CompletableFuture<T>> by tasks {

    constructor() : this(ConcurrentLinkedQueue())

    /**
     * 提交任务
     */
    open fun submitTask(task: CompletableFuture<T>) = task.also { tasks += it }

    open fun CompletableFuture<T>.submit() = submitTask(this)

    /**
     * 创建异步任务并提交
     */
    open fun submitAsync(function: Supplier<T>) = submitTask(CompletableFuture.supplyAsync(function))

    open fun submitAsync(executor: Executor, function: Supplier<T>) = submitTask(CompletableFuture.supplyAsync(function, executor))

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