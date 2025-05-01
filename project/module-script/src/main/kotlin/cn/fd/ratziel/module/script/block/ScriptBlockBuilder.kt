package cn.fd.ratziel.module.script.block

import cn.fd.ratziel.module.script.block.provided.ScriptBlock
import kotlinx.serialization.json.JsonElement

/**
 * ScriptBlockBuilder
 *
 * @author TheFloodDragon
 * @since 2025/4/5 12:56
 */
object ScriptBlockBuilder {

    fun build(element: JsonElement): ExecutableBlock {
        // 开始解析
        val block = ScriptBlock.Parser().parse(element)
        // 返回存在的结果
        if (block != null) return block
        throw Exception("Cannot parse the element to block. Source: $element")
    }

}