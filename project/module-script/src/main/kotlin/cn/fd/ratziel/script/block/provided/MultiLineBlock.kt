package cn.fd.ratziel.script.block.provided

import cn.fd.ratziel.script.api.ScriptEnvironment
import cn.fd.ratziel.script.block.BlockManager
import cn.fd.ratziel.script.block.BlockParser
import cn.fd.ratziel.script.block.ExecutableBlock
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

    override fun execute(environment: ScriptEnvironment): Any? {
        var result: Any? = null
        for (line in lines) {
            result = line.execute(environment)
        }
        return result
    }

    object Parser : BlockParser {

        override fun parse(element: JsonElement) =
            if (element is JsonArray) {
                MultiLineBlock(element.map { BlockManager.parse(it) })
            } else null

    }

}