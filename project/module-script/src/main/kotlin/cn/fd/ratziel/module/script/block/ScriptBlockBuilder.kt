package cn.fd.ratziel.module.script.block

import cn.fd.ratziel.module.script.block.provided.ConditionBlock
import cn.fd.ratziel.module.script.block.provided.ScriptBlock
import cn.fd.ratziel.module.script.block.provided.ValueBlock
import kotlinx.serialization.json.JsonElement
import java.util.function.Supplier

/**
 * ScriptBlockBuilder
 *
 * @author TheFloodDragon
 * @since 2025/4/5 12:56
 */
object ScriptBlockBuilder : BlockParser {

    /**
     * 语句块解析器注册表
     */
    val registry: MutableList<Supplier<BlockParser>> = mutableListOf(
        Supplier { ConditionBlock.Parser },
        Supplier { ScriptBlock.Parser() },
        Supplier { ValueBlock.Parser },
    )

    fun build(element: JsonElement): ExecutableBlock {
        // 开始解析
        val block = ScriptBlock.Parser().parse(element, this)
        // 返回存在的结果
        return block ?: throw Exception("Cannot parse the element to block. Source: $element")
    }

    override fun parse(element: JsonElement, parent: BlockParser): ExecutableBlock {
        // 开始解析
        var result: ExecutableBlock? = null
        for (parser in registry) {
            result = parser.get().parse(element, this) ?: continue
        }
        // 返回存在的结果
        return result ?: throw Exception("Cannot parse the element to block. Source: $element")
    }

}