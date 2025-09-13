package cn.fd.ratziel.core.functional

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * MutexedValue
 *
 * @author TheFloodDragon
 * @since 2025/5/14 21:06
 */
abstract class MutexedValue<T> {

    /**
     * 值是否被锁住
     */
    abstract val isLocked: Boolean

    /**
     * 获取值并锁住现有值
     */
    abstract suspend fun take(): T

    /**
     * 释放值 (锁住值的锁)
     */
    abstract suspend fun release()

    /**
     * 获取值并处理, 在此期间只有一个线程可以操作
     */
    suspend fun <R> withValue(block: suspend (T) -> R): R {
        return try {
            block(take())
        } finally {
            release()
        }
    }

    /**
     * 获取值并处理, 在此期间只有一个线程可以操作
     * @see withValue
     */
    suspend inline operator fun <R> invoke(noinline block: suspend (T) -> R): R = this.withValue(block)

    /**
     * 直接获取值
     */
    suspend fun getValue(): T {
        return withValue { it }
    }

    /**
     * Mutable
     */
    abstract class Mutable<T> : MutexedValue<T>() {

        /**
         * 更新值
         */
        abstract suspend fun update(block: suspend (T) -> T)

    }

    companion object {

        /**
         * 创建一个 [MutexedValue] 的实例
         */
        @JvmStatic
        fun <T> getter(getter: () -> T): MutexedValue<T> = GetterValue(getter)

        /**
         * 创建一个 [MutexedValue.Mutable] 的实例
         */
        @JvmStatic
        fun <T> initial(initialValue: T): Mutable<T> = InitialValue(initialValue)

    }

    private class GetterValue<T>(private val getter: () -> T) : MutexedValue<T>() {
        private val mutex: Mutex = Mutex()
        override val isLocked get() = mutex.isLocked
        override suspend fun release() = mutex.unlock(null)
        override suspend fun take(): T {
            mutex.lock(null)
            return getter()
        }
    }

    private class InitialValue<T>(initialValue: T) : Mutable<T>() {
        private var value = initialValue
        private val mutex: Mutex = Mutex()
        override val isLocked get() = mutex.isLocked
        override suspend fun release() = mutex.unlock(null)
        override suspend fun update(block: suspend (T) -> T) = mutex.withLock { this.value = block(value) }
        override suspend fun take(): T {
            mutex.lock(null)
            return value
        }
    }

}
