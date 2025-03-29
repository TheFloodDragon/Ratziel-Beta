package cn.fd.ratziel.core.function

import kotlinx.serialization.KSerializer

/**
 * ContextualSerializer
 *
 * @author TheFloodDragon
 * @since 2025/3/22 16:11
 */
interface ContextualSerializer<T> : KSerializer<T> {

    /**
     * 接受上下文
     *
     * @param context 要接受的上下文
     * @return 一个新的序列化器 [KSerializer]
     */
    fun accept(context: ArgumentContext): KSerializer<T>

}