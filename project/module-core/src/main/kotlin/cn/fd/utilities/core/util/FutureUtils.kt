package cn.fd.utilities.core.util

import java.util.concurrent.CompletableFuture

fun <U> future(function: () -> U): CompletableFuture<U> {
    return CompletableFuture.supplyAsync(function)
}