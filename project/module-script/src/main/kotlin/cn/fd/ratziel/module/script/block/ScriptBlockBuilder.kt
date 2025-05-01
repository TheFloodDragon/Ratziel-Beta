package cn.fd.ratziel.module.script.block

import cn.fd.ratziel.module.script.block.provided.ConditionBlock
import cn.fd.ratziel.module.script.block.provided.ScriptBlock
import cn.fd.ratziel.module.script.block.provided.ValueBlock
import kotlinx.serialization.json.JsonElement

/**
 * ScriptBlockBuilder
 *
 * @author TheFloodDragon
 * @since 2025/4/5 12:56
 */
class ScriptBlockBuilder : BlockParser {

    /**
     * 语句块解析器注册表
     */
    val registry: MutableList<BlockParser> = mutableListOf(
        ConditionBlock.Parser,
        ScriptBlock.Parser(),
        ValueBlock.Parser,
    )

    override fun parse(element: JsonElement, parser: BlockParser): ExecutableBlock {
        for (p in registry) {
            // 开始解析
            val block = p.parse(element)
            // 返回存在的结果
            if (block != null) return block
        }
        throw Exception("Cannot parse the element to block. Source: $element")
    }

    companion object {

        /**
         * 构建语句块
         * 无法解析时抛出异常
         */
        fun build(element: JsonElement): ExecutableBlock {
            val builder = ScriptBlockBuilder()
            return builder.parse(element, builder)
        }

    }

}