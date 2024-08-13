package cn.fd.ratziel.function

import cn.fd.ratziel.function.internal.FutureLazyImpl
import java.util.concurrent.Future

/**
 * 将 [Future] 转为 [Lazy]
 */
fun <T> Future<T>.asLazy(): Lazy<T> = FutureLazyImpl(this)

/**
 * 获取 [Lazy] 的值
 * 未初始化时返回为空
 */
fun <T> Lazy<T>.getOrNull(): T? = if (isInitialized()) value else null

/**
 * 获取 [Lazy] 的值
 * 未初始化时返回为默认值
 */
fun <T> Lazy<T>.getOr(default: T): T = if (isInitialized()) value else default