package cn.fd.utilities.core.util

import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

fun <U> future(function: Supplier<U>): CompletableFuture<U> {
    return CompletableFuture.supplyAsync(function)
}

fun runFuture(function: Runnable): CompletableFuture<Void> {
    return CompletableFuture.runAsync(function)
}