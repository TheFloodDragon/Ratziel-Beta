package cn.fd.ratziel.module.script.block

import cn.fd.ratziel.module.script.block.provided.ConditionBlock
import cn.fd.ratziel.module.script.block.provided.MultiLineBlock
import cn.fd.ratziel.module.script.block.provided.ScriptBlock
import cn.fd.ratziel.module.script.block.provided.ValueBlock
import kotlinx.serialization.json.JsonElement
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Supplier

/**
 * BlockBuilder
 *
 * @author TheFloodDragon
 * @since 2025/4/5 12:56
 */
object BlockBuilder {

    /**
     * 语句块解析器注册表
     */
    val registry: MutableList<Supplier<BlockParser>> = CopyOnWriteArrayList(
        listOf(
            Supplier { ConditionBlock.Parser },
            Supplier { ScriptBlock.Parser() },
            Supplier { MultiLineBlock.Parser },
            Supplier { ValueBlock.Parser },
        )
    )

    /**
     * 构建语句块
     *
     * @param element 语句块元素
     * @return 解析后的语句块
     */
    fun build(element: JsonElement): ExecutableBlock {
        // 创建调度器
        val scheduler = BlockScheduler(registry.map { it.get() })
        // 调用调度器解析
        val result = scheduler.parse(element, scheduler)
        // 返回解析结果
        return result ?: throw Exception("Cannot parse the element to block. Source: $element")
    }

    class BlockScheduler(
        /**
         * 使用的解析器列表
         */
        val parsers: List<BlockParser>,
    ) : BlockParser {

        override fun parse(element: JsonElement, scheduler: BlockParser): ExecutableBlock? {
            // 开始解析
            var result: ExecutableBlock? = null
            // 挨个尝试解析
            for (parser in parsers) {
                // 默认调度器使用自身
                result = parser.parse(element, this) ?: continue
                break // 解析到有效语句块就返回
            }
            // 返回存在的结果
            return result
        }

    }

    /**
     * 注册语句块解析器
     *
     * @param parser 语句块解析器获取器
     */
    fun register(parser: Supplier<BlockParser>) {
        registry.add(parser)
    }

    /**
     * 注册语句块解析器
     *
     * @param parser 语句块解析器
     */
    fun register(parser: BlockParser) {
        registry.add { parser }
    }

}