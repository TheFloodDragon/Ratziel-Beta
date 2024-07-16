package cn.fd.ratziel.script

import cn.fd.ratziel.script.api.*
import cn.fd.ratziel.script.impl.SimpleScript


/**
 * ScriptBlockBuilder
 *
 * @author TheFloodDragon
 * @since 2024/6/30 16:55
 */
object ScriptBlockBuilder {

    fun build(section: Any, executor: ScriptExecutor): ScriptBlock {
        when (section) {
            // BasicBlock
            is String -> return BasicBlock(section, executor)
            // ListBlock
            is Iterable<*> -> return ListBlock(section.mapNotNull { l -> l?.let { build(it, executor) } }, executor)
            is Map<*, *> -> {
                // ConditionBlock
                val ifValue = section["if"] ?: section["condition"]
                if (ifValue != null) {
                    val thenValue = section["then"]
                    val elseValue = section["else"]
                    return ConditionBlock(
                        build(ifValue, executor),
                        thenValue?.let { build(it, executor) },
                        elseValue?.let { build(it, executor) },
                        executor
                    )
                } else {
                    // OverrideExecutorBlock
                    for (e in section) {
                        val key = e.key.toString().trim()
                        if (key.startsWith(MARK_TOGGLE)) {
                            val type = ScriptTypes.matchOrThrow(key.drop(MARK_TOGGLE.length))
                            val value = e.value
                            if (value != null) return build(value, type.executor)
                        }
                    }
                }
            }
            // PrimitiveBlock
            else -> return PrimitiveBlock(section, executor)
        }
        return PrimitiveBlock(null, executor)
    }

    const val MARK_TOGGLE = "\$"

    abstract class ScriptBlock(private val exe: ScriptExecutor) : EvaluableScript {
        override fun getExecutor() = exe
    }

    class BasicBlock(val script: ScriptContent, executor: ScriptExecutor) : ScriptBlock(executor) {
        constructor(content: String, executor: ScriptExecutor) : this(SimpleScript(content, executor), executor)

        init {
            // 预编译
            if (script is StorableScript) script.compile(executor)
        }

        override fun evaluate(enviornment: ScriptEnvironment): Any? =
            if (script is EvaluableScript) script.evaluate(enviornment) else executor.evaluate(script, enviornment)
    }

    class PrimitiveBlock(val value: Any?, executor: ScriptExecutor) : ScriptBlock(executor) {
        override fun evaluate(enviornment: ScriptEnvironment) = value
    }

    class ListBlock(val array: Array<ScriptBlock>, executor: ScriptExecutor) : ScriptBlock(executor) {
        constructor(list: Iterable<ScriptBlock>, executor: ScriptExecutor) : this(buildList {
            var combined = mutableListOf<String>()
            fun combine() {
                if (combined.isEmpty()) return
                // 将脚本列表通过换行符合并成单个脚本字符串
                val lined = combined.joinToString("\n")
                // 清空
                combined = mutableListOf()
                // 然后添加基础代码块
                add(BasicBlock(lined, executor))
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
        }.toTypedArray(), executor)

        override fun evaluate(enviornment: ScriptEnvironment): Any? {
            val iterator = array.iterator()
            while (iterator.hasNext()) {
                val result = iterator.next().evaluate(enviornment)
                // 在没有下一个元素(最后一个脚本执行完后), 返回结果
                if (!iterator.hasNext()) return result
            }
            return null
        }
    }

    class ConditionBlock(
        val ifBlock: ScriptBlock,
        val thenBlock: ScriptBlock?,
        val elseBlock: ScriptBlock?,
        executor: ScriptExecutor
    ) : ScriptBlock(executor) {
        override fun evaluate(environment: ScriptEnvironment): Any? {
            return if (ifBlock.evaluate(environment) == true) thenBlock?.evaluate(environment) else elseBlock?.evaluate(environment)
        }
    }

}