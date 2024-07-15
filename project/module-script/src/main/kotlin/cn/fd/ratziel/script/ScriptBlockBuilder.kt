package cn.fd.ratziel.script

import cn.fd.ratziel.script.api.EvaluableScript
import cn.fd.ratziel.script.api.ScriptContent
import cn.fd.ratziel.script.api.ScriptEnvironment
import cn.fd.ratziel.script.api.ScriptExecutor
import cn.fd.ratziel.script.impl.SimpleScript


/**
 * ScriptBlockBuilder
 *
 * @author TheFloodDragon
 * @since 2024/6/30 16:55
 */
object ScriptBlockBuilder {

    fun build(section: Any): ScriptBlock {
        when (section) {
            // Block
            is String -> return Block(SimpleScript(section))
            // ListBlock
            is Iterable<*> -> return ListBlock(section.mapNotNull { l -> l?.let { build(it) } })
            is Map<*, *> -> {
                // ConditionBlock
                val ifValue = section["if"] ?: section["condition"]
                if (ifValue != null) {
                    val thenValue = section["then"]
                    val elseValue = section["else"]
                    return ConditionBlock(
                        build(ifValue),
                        thenValue?.let { build(it) },
                        elseValue?.let { build(it) }
                    )
                } else {
                    // OverrideExecutorBlock
                    for (e in section) {
                        val key = e.key.toString().trim()
                        if (key.startsWith(MARK_TOGGLE)) {
                            val type = ScriptTypes.matchOrThrow(key.drop(MARK_TOGGLE.length))
                            val value = e.value
                            if (value != null) return OverrideExecutorBlock(type.executor, build(value))
                        }
                    }
                }
            }
            // PrimitiveBlock
            else -> return PrimitiveBlock(section)
        }
        return PrimitiveBlock(null)
    }

    const val MARK_TOGGLE = "\$"

    interface ScriptBlock : EvaluableScript

    data class Block(val content: ScriptContent) : ScriptBlock {
        override fun evaluate(executor: ScriptExecutor, env: ScriptEnvironment): Any? = executor.evaluate(content, env)
    }

    data class PrimitiveBlock(val value: Any?) : ScriptBlock {
        override fun evaluate(executor: ScriptExecutor, env: ScriptEnvironment) = value
    }

    data class ListBlock(val list: Iterable<ScriptBlock>) : ScriptBlock {
        override fun evaluate(executor: ScriptExecutor, env: ScriptEnvironment): Any? {
            var result: Any? = null
            for (raw in list) {
                result = raw.evaluate(executor, env)
            }
            return result
        }
    }

    data class OverrideExecutorBlock(val newExecutor: ScriptExecutor, val block: ScriptBlock) : ScriptBlock {
        override fun evaluate(executor: ScriptExecutor, env: ScriptEnvironment): Any? = evaluate(env)
        fun evaluate(env: ScriptEnvironment): Any? = block.evaluate(newExecutor, env)
    }

    data class ConditionBlock(
        val ifBlock: ScriptBlock,
        val thenBlock: ScriptBlock?,
        val elseBlock: ScriptBlock?
    ) : ScriptBlock {
        override fun evaluate(executor: ScriptExecutor, env: ScriptEnvironment): Any? {
            return if (ifBlock.evaluate(executor, env) == true) thenBlock?.evaluate(executor, env) else elseBlock?.evaluate(executor, env)
        }
    }

}