package cn.fd.ratziel.common.block.provided

import cn.fd.ratziel.common.block.BlockContext
import cn.fd.ratziel.common.block.BlockParser
import cn.fd.ratziel.common.block.ExecutableBlock
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.util.adapt
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * ValueBlock
 *
 * @author TheFloodDragon
 * @since 2025/4/5 12:00
 */
class ValueBlock(val value: Any?) : ExecutableBlock {

    override fun execute(context: ArgumentContext) = value

    object Parser : BlockParser {
        override fun parse(element: JsonElement, context: BlockContext): ExecutableBlock? {
            return if (element is JsonPrimitive) ValueBlock(element.contentOrNull?.adapt()) else null
        }
    }

}