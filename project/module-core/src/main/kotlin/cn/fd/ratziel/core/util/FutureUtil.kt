package cn.fd.ratziel.core.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Supplier

fun <T> future(function: Supplier<T>): CompletableFuture<T> {
    return CompletableFuture.supplyAsync(function)
}

fun <T> future(executor: Executor, function: Supplier<T>): CompletableFuture<T> {
    return CompletableFuture.supplyAsync(function, executor)
}

fun runFuture(function: Runnable): CompletableFuture<Void> {
    return CompletableFuture.runAsync(function)
}

fun runFuture(executor: Executor, function: Runnable): CompletableFuture<Void> {
    return CompletableFuture.runAsync(function, executor)
}