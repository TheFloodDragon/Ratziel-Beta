package cn.fd.ratziel.common.block.provided

import cn.fd.ratziel.common.block.BlockContext
import cn.fd.ratziel.common.block.BlockParser
import cn.fd.ratziel.common.block.ExecutableBlock
import cn.fd.ratziel.core.contextual.ArgumentContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

/**
 * MultiLineBlock
 *
 * @author TheFloodDragon
 * @since 2024/10/2 18:28
 */
class MultiLineBlock(val blocks: List<ExecutableBlock>) : ExecutableBlock {

    override fun execute(context: ArgumentContext): Any? {
        var result: Any? = null
        for (block in blocks) {
            result = block.execute(context)
        }
        return result
    }

    object Parser : BlockParser {
        override fun parse(element: JsonElement, context: BlockContext): ExecutableBlock? {
            if (element is JsonArray) {
                // 解析多行脚本块
                val blocks = element.map {
                    context.parse(it)
                }
                // 返回有效的 MultiLineBlock
                if (blocks.isNotEmpty()) {
                    return MultiLineBlock(blocks)
                }
            }
            return null
        }
    }

}