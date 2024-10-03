package cn.fd.ratziel.script.block.provided

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.script.block.BlockParser
import cn.fd.ratziel.script.block.ExecutableBlock
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * PrimitiveBlock
 *
 * @author TheFloodDragon
 * @since 2024/10/2 18:42
 */
class PrimitiveBlock(val value: Any?) : ExecutableBlock {

    override fun execute(context: ArgumentContext) = value

    object Parser : BlockParser {

        override fun parse(element: JsonElement) = if (element is JsonPrimitive) PrimitiveBlock(element.contentOrNull) else null

    }

}