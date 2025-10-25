package cn.fd.ratziel.common.block

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.element.Element
import kotlinx.serialization.json.JsonElement

/**
 * BlockBuilder
 *
 * @author TheFloodDragon
 * @since 2025/4/5 12:56
 */
object BlockBuilder {

    /**
     * 构建语句块
     */
    @JvmStatic
    @JvmOverloads
    fun build(element: Element, vararg scopes: BlockScope, contextApplier: BlockContext.() -> Unit = {}): ExecutableBlock {
        val sequentialScopes = if (scopes.isNotEmpty()) {
            scopes.map { scope ->
                scope as? BlockScope.PrioritizedScope ?: BlockScope.PrioritizedScope(0, scope)
            }.sortedBy { it.priority }
        } else BlockScope.registry
        return this.build(element.property, sequentialScopes) {
            workFile = element.file
            contextApplier()
        }
    }

    /**
     * 构建语句块
     *
     * @param element 语句块元素
     * @return 解析后的语句块
     */
    @JvmStatic
    private fun build(element: JsonElement, scopes: Iterable<BlockScope>, contextApplier: BlockContext.() -> Unit): ExecutableBlock {
        // 排序获取顺序解析器列表
        val sequentialParsers = scopes.flatMap { it.sequentialParsers }
        // 创建调度器
        val scheduler = BlockScheduler(sequentialParsers)
        // 带有调度器的上下文
        val context = BlockContext(scheduler)
        contextApplier(context)
        // 调用调度器解析并返回解析结果
        return ExecutionEntrance(context.parse(element), context) // 返回执行入口
    }

    class BlockScheduler(
        /**
         * 使用的解析器列表
         */
        val parsers: List<BlockParser>,
    ) : BlockParser {

        override fun parse(element: JsonElement, context: BlockContext): ExecutableBlock? {
            // 开始解析
            var result: ExecutableBlock? = null
            // 挨个尝试解析
            for (parser in parsers) {
                // 默认调度器使用自身
                result = parser.parse(element, context) ?: continue
                break // 解析到有效语句块就返回
            }
            // 返回存在的结果
            return result
        }

    }

    private class ExecutionEntrance(
        val run: ExecutableBlock,
        val ctx: BlockContext,
    ) : ExecutableBlock {
        override fun execute(context: ArgumentContext): Any? {
            val context = if (ctx.copyContext) context.copy() else context
            ctx.onStart.invoke(context)
            val result = run.execute(context)
            return ctx.onEnd.invoke(context, result) ?: result
        }
    }

}