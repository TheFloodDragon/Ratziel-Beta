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
            // BasicBlock
            is String -> return BasicBlock(section)
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

    data class BasicBlock(val script: ScriptContent) : ScriptBlock {
        constructor(content: String) : this(SimpleScript(content))

        override fun evaluate(executor: ScriptExecutor, env: ScriptEnvironment): Any? =
            if (script is EvaluableScript) script.evaluate(executor, env) else executor.evaluate(script, env)
    }

    data class PrimitiveBlock(val value: Any?) : ScriptBlock {
        override fun evaluate(executor: ScriptExecutor, env: ScriptEnvironment) = value
    }

    data class ListBlock(val list: Iterable<ScriptBlock>) : ScriptBlock {
        val handled = buildList {
            var combined = mutableListOf<String>()
            fun combine() {
                if (combined.isEmpty()) return
                // 将脚本列表通过换行符合并成单个脚本字符串
                val lined = combined.joinToString("\n")
                // 清空
                combined = mutableListOf()
                // 然后添加基础代码块
                add(BasicBlock(lined))
            }
            for (block in list) {
                if (block is BasicBlock) {
                    combined.add(block.script.content)
                } else {
                    // 非基础代码块检查合并一次
                    combine()
                    // 加入代码块
                    add(block)
                }
            }
            // 尾处理: 检查合并一次
            combine()
        }.toTypedArray()

        override fun evaluate(executor: ScriptExecutor, env: ScriptEnvironment): Any? {
            val iterator = handled.iterator()
            while (iterator.hasNext()) {
                val result = iterator.next().evaluate(executor, env)
                // 在没有下一个元素(最后一个脚本执行完后), 返回结果
                if (!iterator.hasNext()) return result
            }
            return null
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