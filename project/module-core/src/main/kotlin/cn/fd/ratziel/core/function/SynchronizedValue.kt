package cn.fd.ratziel.core.function

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * SynchronizedValue
 *
 * @author TheFloodDragon
 * @since 2025/5/14 21:06
 */
abstract class SynchronizedValue<T> {

    /**
     * Kotlin 协程锁 [Mutex]
     */
    protected val mutex: Mutex = Mutex()

    /**
     * 获取值并处理, 在此期间只有一个线程可以操作
     */
    abstract suspend fun <R> withValue(block: suspend (T) -> R): R

    /**
     * 直接获取值
     */
    open suspend fun getValue(): T {
        return withValue { it }
    }

    /**
     * Mutable
     */
    abstract class Mutable<T> : SynchronizedValue<T>() {

        /**
         * 更新值
         */
        abstract suspend fun update(block: suspend (T) -> T)

    }

    companion object {

        /**
         * 创建一个 [SynchronizedValue] 的实例
         */
        fun <T> getter(getter: () -> T): SynchronizedValue<T> = GetterValue(getter)

        /**
         * 创建一个 [SynchronizedValue.Mutable] 的实例
         */
        fun <T> initial(initialValue: T): Mutable<T> = InitialValue(initialValue)

    }

    private class GetterValue<T>(private val getter: () -> T) : SynchronizedValue<T>() {
        override suspend fun <R> withValue(block: suspend (T) -> R): R {
            return mutex.withLock {
                // 在锁的保护下执行处理逻辑
                block(getter.invoke())
            }
        }
    }

    private class InitialValue<T>(initialValue: T) : Mutable<T>() {
        private var value = initialValue

        override suspend fun <R> withValue(block: suspend (T) -> R): R {
            return mutex.withLock {
                // 在锁的保护下执行处理逻辑
                block(value)
            }
        }

        override suspend fun update(block: suspend (T) -> T) {
            mutex.withLock { value = block(value) }
        }
    }

}