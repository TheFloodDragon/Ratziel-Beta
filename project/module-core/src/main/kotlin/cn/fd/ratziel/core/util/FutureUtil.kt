package cn.fd.ratziel.core.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Supplier

fun <T> quickFuture(function: Supplier<T>) = CompletableFuture.supplyAsync(function)!!

fun <T> quickFuture(executor: Executor, function: Supplier<T>) = CompletableFuture.supplyAsync(function, executor)!!

fun quickRunFuture(function: Runnable) = CompletableFuture.runAsync(function)!!

fun quickRunFuture(executor: Executor, function: Runnable): CompletableFuture<Void> = CompletableFuture.runAsync(function, executor)