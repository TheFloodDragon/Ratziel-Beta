package cn.fd.ratziel.core.function

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.function.Supplier

/**
 * SynchronizedValue
 *
 * @author TheFloodDragon
 * @since 2025/5/14 21:06
 */
class SynchronizedValue<T>(
    /**
     * 值获取器
     */
    private val getter: Supplier<T>
) {

    /**
     * Kotlin 协程锁 [Mutex]
     */
    private val mutex = Mutex()

    /**
     * 获取值并处理, 在此期间只有一个线程可以操作
     */
    suspend fun <R> withValue(block: suspend (T) -> R): R {
        return mutex.withLock {
            // 在锁的保护下执行处理逻辑
            block(getter.get())
        }
    }

}