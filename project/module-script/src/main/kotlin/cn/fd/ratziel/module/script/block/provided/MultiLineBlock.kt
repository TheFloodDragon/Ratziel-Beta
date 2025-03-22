package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.script.block.BlockParser
import cn.fd.ratziel.module.script.block.ExecutableBlock
import cn.fd.ratziel.module.script.block.RecursingBlockParser
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

/**
 * MultiLineBlock
 *
 * @author TheFloodDragon
 * @since 2024/10/2 18:28
 */
class MultiLineBlock(
    val lines: Iterable<ExecutableBlock>
) : ExecutableBlock {

    override fun execute(context: ArgumentContext): Any? {
        var result: Any? = null
        for (line in lines) {
            result = line.execute(context)
        }
        return result
    }

    object Parser : RecursingBlockParser {

        override fun parse(element: JsonElement, parser: BlockParser) =
            if (element is JsonArray) {
                MultiLineBlock(element.map { parser.parse(it)!! })
            } else null

    }

}