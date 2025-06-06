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
open class BlockBuilder(
    /**
     * 使用的解析器列表
     */
    protected open val parsers: List<BlockParser>,
) : BlockParser {

    fun build(element: JsonElement): ExecutableBlock {
        // 开始解析
        var result: ExecutableBlock? = null
        // 挨个尝试解析
        for (parser in parsers) {
            // 默认调度器使用自身
            result = parser.parse(element, this) ?: continue
        }
        // 返回存在的结果
        return result ?: throw Exception("Cannot parse the element to block. Source: $element")
    }

    override fun parse(element: JsonElement, scheduler: BlockParser) = build(element)

    companion object Default : BlockBuilder(emptyList()) {

        /**
         * 语句块解析器注册表
         */
        val registry: MutableList<Supplier<BlockParser>> = CopyOnWriteArrayList(
            listOf(
                Supplier { ConditionBlock.Parser },
                Supplier { MultiLineBlock.Parser },
                Supplier { ScriptBlock.Parser() },
                Supplier { ValueBlock.Parser },
            )
        )

        override val parsers: List<BlockParser> get() = registry.map { it.get() }

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

}