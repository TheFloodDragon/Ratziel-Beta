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
open class FutureFactory<T>(
    /**
     * 任务列表
     */
    protected open val tasks: MutableCollection<CompletableFuture<T>>
) : MutableCollection<CompletableFuture<T>> by tasks {

    constructor() : this(ConcurrentLinkedQueue())

    /**
     * 获取 [FutureFactory] 保存的所有任务
     */
    open fun getAllTasks(): Collection<CompletableFuture<T>> = tasks

    /**
     * 清空[FutureFactory] 保存的所有任务
     */
    open fun clearAllTasks() = tasks.clear()

    /**
     * 提交任务
     */
    open fun submitTask(task: CompletableFuture<T>) = task.also { tasks += it }

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
    open fun <R> thenApply(action: Function<List<T>, R>): CompletableFuture<R> = tasks.toTypedArray().let { futures ->
        CompletableFuture.allOf(*futures).thenApply {
            val results = futures.map { it.get() } // 获取异步结果
            action.apply(results) // 处理结果
        }
    }

    open fun thenAccept(action: Consumer<List<T>> = Consumer {}) = thenApply { action.accept(it) }

    open fun thenRun(action: Runnable = Runnable {}) = thenApply { action.run() }

    /**
     * 等待所有任务完成 (阻塞)
     */
    open fun waitAll() {
        val future = CompletableFuture.allOf(*tasks.toTypedArray())
        future.join() // 阻塞等待完成
    }

}