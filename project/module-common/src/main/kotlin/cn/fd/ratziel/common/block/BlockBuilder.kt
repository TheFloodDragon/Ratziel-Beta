package cn.fd.ratziel.common.block

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
    fun build(element: Element, scope: BlockScope = BlockScope.DefaultScope, contextApplier: BlockContext.() -> Unit = {}): ExecutableBlock {
        return this.build(element.property, scope) {
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
    private fun build(element: JsonElement, scope: BlockScope, contextApplier: BlockContext.() -> Unit): ExecutableBlock {
        // 创建调度器
        val scheduler = BlockScheduler(scope.parsers)
        // 带有调度器的上下文
        val context = BlockContext(scheduler)
        contextApplier(context)
        // 调用调度器解析并返回解析结果
        return context.parse(element)
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

}