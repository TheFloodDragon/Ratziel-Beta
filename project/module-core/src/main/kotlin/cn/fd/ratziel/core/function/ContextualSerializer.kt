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
     */
    fun accept(context: ArgumentContext): KSerializer<T>

}