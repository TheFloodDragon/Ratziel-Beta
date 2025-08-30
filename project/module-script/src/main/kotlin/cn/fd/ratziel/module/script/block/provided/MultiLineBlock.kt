package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.script.block.BlockParser
import cn.fd.ratziel.module.script.block.ExecutableBlock
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
        override fun parse(element: JsonElement, scheduler: BlockParser): ExecutableBlock? {
            if (element is JsonArray) {
                // 解析多行脚本块
                val blocks = element.mapNotNull {
                    scheduler.parse(it, scheduler)
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