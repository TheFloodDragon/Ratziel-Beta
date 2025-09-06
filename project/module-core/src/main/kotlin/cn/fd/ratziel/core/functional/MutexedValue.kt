package cn.fd.ratziel.core.functional

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * MutexedValue
 *
 * @author TheFloodDragon
 * @since 2025/5/14 21:06
 */
interface MutexedValue<T> {

    /**
     * 获取值并处理, 在此期间只有一个线程可以操作
     */
    suspend fun <R> withValue(block: suspend (T) -> R): R

    /**
     * 获取值并处理, 在此期间只有一个线程可以操作
     * @see withValue
     */
    suspend operator fun <R> invoke(block: suspend (T) -> R): R = this.withValue(block)

    /**
     * 直接获取值
     */
    suspend fun getValue(): T {
        return withValue { it }
    }

    /**
     * Mutable
     */
    interface Mutable<T> : MutexedValue<T> {

        /**
         * 更新值
         */
        suspend fun update(block: suspend (T) -> T)

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

        /**
         * 同时等待多个值, 并处理
         */
        @JvmStatic
        suspend fun <T1, T2, R> withAll(
            m1: MutexedValue<T1>,
            m2: MutexedValue<T2>,
            block: suspend (T1, T2) -> R,
        ): R {
            return m1.withValue { v1 ->
                m2.withValue { v2 -> block(v1, v2) }
            }
        }

    }

    private class GetterValue<T>(private val getter: () -> T) : MutexedValue<T> {
        private val mutex: Mutex = Mutex()
        override suspend fun <R> withValue(block: suspend (T) -> R): R {
            return mutex.withLock {
                // 在锁的保护下执行处理逻辑
                block(getter.invoke())
            }
        }
    }

    private class InitialValue<T>(initialValue: T) : Mutable<T> {
        private var value = initialValue
        private val mutex: Mutex = Mutex()
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
