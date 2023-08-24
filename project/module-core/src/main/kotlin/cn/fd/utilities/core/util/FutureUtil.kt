package cn.fd.utilities.core.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

fun <U> future(function: () -> U): CompletableFuture<U> {
    return CompletableFuture.supplyAsync(function)
}

fun <U> future(executor: Executor,function: () -> U): CompletableFuture<U> {
    return CompletableFuture.supplyAsync(function, executor)
}

fun runFuture(function: Runnable): CompletableFuture<Void> {
    return CompletableFuture.runAsync(function)
}

fun runFuture(executor: Executor,function: Runnable): CompletableFuture<Void> {
    return CompletableFuture.runAsync(function, executor)
}