package cn.fd.ratziel.function.internal

import java.util.concurrent.Future

/**
 * CompletableFutureLazyImpl
 *
 * @author TheFloodDragon
 * @since 2024/8/13 21:07
 */
internal class FutureLazyImpl<out T>(private val future: Future<T>) : Lazy<T> {

    override fun isInitialized() = future.isDone

    override val value: T get() = if (isInitialized()) future.get() else throw UninitializedPropertyAccessException("Lazy value not initialized yet!")

}