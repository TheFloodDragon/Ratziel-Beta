package cn.fd.ratziel.module.script.util

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.script.util.VariablesMap.Companion.transformers
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

/**
 * VariablesMap
 *
 * @author TheFloodDragon
 * @since 2025/6/9 17:15
 */
class VariablesMap(
    private val vars: MutableMap<String, Any?> = hashMapOf(),
) : MutableMap<String, Any?> by vars {

    constructor(action: VariablesMap.() -> Unit) : this() {
        action(this)
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