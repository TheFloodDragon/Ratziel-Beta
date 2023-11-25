package cn.fd.ratziel.core.function

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executor
import java.util.function.Supplier

fun <T> quickFuture(function: Supplier<T>) = CompletableFuture.supplyAsync(function)!!

fun <T> quickFuture(executor: Executor, function: Supplier<T>) = CompletableFuture.supplyAsync(function, executor)!!

fun quickRunFuture(function: Runnable) = CompletableFuture.runAsync(function)!!

fun quickRunFuture(executor: Executor, function: Runnable): CompletableFuture<Void> =
    CompletableFuture.runAsync(function, executor)

/**
 * 简化多异步任务的过程
 */
fun <T> futureFactory(block: FutureFactory<T>.() -> Unit) = FutureFactory<T>().also { block(it) }

@JvmName("futureFactoryUnit")
fun futureFactory(block: FutureFactory<Unit>.() -> Unit) = futureFactory<Unit>(block)

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
    fun newAsync(function: Supplier<T>) = submitFuture(quickFuture(function))

    /**
     * 创建一个 [CompletableFuture]
     */
    fun newFuture() = submitFuture(CompletableFuture<T>())

    /**
     * 等待所有任务完成
     */
    fun waitForAll() = this.apply {
        packedFuture.join()
    }

    protected val packedFuture
        get() = CompletableFuture.allOf(*this.toTypedArray())

}