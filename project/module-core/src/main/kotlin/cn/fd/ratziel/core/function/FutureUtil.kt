package cn.fd.ratziel.core.function

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executor
import java.util.function.Consumer
import java.util.function.Supplier

fun <T> futureAsync(function: Supplier<T>) = CompletableFuture.supplyAsync(function)!!

fun <T> futureAsync(executor: Executor, function: Supplier<T>) = CompletableFuture.supplyAsync(function, executor)!!

fun futureRunAsync(function: Runnable) = CompletableFuture.runAsync(function)!!

fun futureRunAsync(executor: Executor, function: Runnable): CompletableFuture<Void> =
    CompletableFuture.runAsync(function, executor)

/**
 * 简化多异步任务的过程
 */
fun <T> futureFactory(block: FutureFactory<T>.() -> Unit) = FutureFactory<T>().apply(block)

fun futureFactoryAny(block: FutureFactory<Any?>.() -> Unit) = futureFactory(block)

/**
 * FutureFactory - 用于管控多 [CompletableFuture] 的任务
 *
 * @author TheFloodDragon
 * @since 2023/11/19 11:27
 */
open class FutureFactory<T> : ConcurrentLinkedDeque<CompletableFuture<T>>() {

    /**
     * 提交任务
     */
    fun submitFuture(task: CompletableFuture<T>) = task.also { this += it }

    /**
     * 异步执行任务
     */
    fun newAsync(function: Supplier<T>) = submitFuture(futureAsync(function))

    /**
     * 创建一个 [CompletableFuture]
     */
    fun newFuture() = submitFuture(CompletableFuture<T>())

    /**
     * 等待所有任务完成 (阻塞)
     */
    fun wait() = pack().join()

    /**
     * 当所有任务完成时 (非阻塞)
     * 并提供所有任务返回值的列表
     * @param function 对返回值进行操作的函数
     */
    fun whenFinished(function: Consumer<List<T>>) = this.toTypedArray().let { futures ->
        CompletableFuture.allOf(*futures).thenApply { _ ->
            futures.map { it.get() }.also { function.accept(it) }
        }
    }

    /**
     * 将所有任务包装在一个任务上
     * @param whenAll 若为真,则此任务会在所有子任务完成后完成
     */
    fun pack(whenAll: Boolean = true) =
        if (whenAll) CompletableFuture.allOf(*this.toTypedArray())
        else CompletableFuture.anyOf(*this.toTypedArray())

}