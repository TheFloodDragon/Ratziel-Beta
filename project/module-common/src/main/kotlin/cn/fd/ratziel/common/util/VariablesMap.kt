package cn.fd.ratziel.common.util

import cn.fd.ratziel.common.util.VariablesMap.Companion.transformers
import cn.fd.ratziel.core.contextual.ArgumentContext
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.BiConsumer

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
        transformers.forEach { it.accept(this, context) }
    }

    override fun toString() = "VariablesMap$vars"

    companion object {

        /**
         * Transformer 机制: 从 [ArgumentContext] 中获取 [VariablesMap] 在实际运用中可能用到的变量
         */
        val transformers: MutableList<BiConsumer<VariablesMap, ArgumentContext>> = CopyOnWriteArrayList()

    }

}

/**
 * 从 [ArgumentContext] 中获取 [VariablesMap]
 */
fun ArgumentContext.varsMap(): VariablesMap =
    popOr(VariablesMap::class.java) {
        val vars = VariablesMap()
        // 加入到上下文中
        this.put(vars)
        // 接受上下文, 将上下文中的参数转化为变量导入 vars
        vars.accept(this)
        return@popOr vars
    }
