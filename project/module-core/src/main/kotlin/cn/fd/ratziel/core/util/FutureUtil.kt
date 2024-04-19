@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.core.util

import java.util.concurrent.CompletableFuture


/**
 * 捕获异常并打印
 */
inline fun <T> CompletableFuture<T?>.printOnException(): CompletableFuture<T?> = this.exceptionally { it.printStackTrace();null }