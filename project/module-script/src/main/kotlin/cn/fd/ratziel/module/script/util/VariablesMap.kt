package cn.fd.ratziel.module.script.util

import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.module.script.util.VariablesMap.Companion.transformers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

/**
 * VariablesMap
 *
 * @author TheFloodDragon
 * @since 2025/6/9 17:15
 */
class VariablesMap(
    private val vars: MutableMap<String, Any> = ConcurrentHashMap(),
) : MutableMap<String, Any> by vars {

    constructor(action: VariablesMap.() -> Unit) : this() {
        action(this)
    }

    @JvmName("putNonNullValues")
    fun putValues(from: Map<String, Any?>) {
        for ((k, v) in from) if (v != null) put(k, v)
    }

    /**
     * Transformer 机制: 接受 [context] 并使用 [transformers] 转化变量
     */
    fun accept(context: ArgumentContext) {
        transformers.forEach { it.accept(context) }
    }

    companion object {

        /**
         * Transformer 机制: 从 [ArgumentContext] 中获取 [VariablesMap] 在实际运用中可能用到的变量
         */
        val transformers: MutableList<Consumer<ArgumentContext>> = CopyOnWriteArrayList()

    }

}