package cn.fd.ratziel.core.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executor
import java.util.function.Consumer
import java.util.function.Supplier

fun <T> quickFuture(function: Supplier<T>) = CompletableFuture.supplyAsync(function)!!

fun <T> quickFuture(executor: Executor, function: Supplier<T>) = CompletableFuture.supplyAsync(function, executor)!!

fun quickRunFuture(function: Runnable) = CompletableFuture.runAsync(function)!!

fun quickRunFuture(executor: Executor, function: Runnable): CompletableFuture<Void> =
    CompletableFuture.runAsync(function, executor)

/**
 * 快速获取一个FutureFactory
 */
fun <T> futureFactory(function: Consumer<FutureFactory<T>>) = function.accept(FutureFactory())

/**
 * 用于多异步任务的控制
 */
open class FutureFactory<T> : ConcurrentLinkedDeque<CompletableFuture<T>>() {

    /**
     * 提交任务
     */
    fun submit(task: CompletableFuture<T>) {
        this += task
    }

    fun submit(function: Supplier<T>) {
        this += quickFuture(function)
    }

    /**
     * 等待所有任务完成
     */
    fun waitForAll() {
        CompletableFuture.allOf(*this.toTypedArray()).join()
    }

}