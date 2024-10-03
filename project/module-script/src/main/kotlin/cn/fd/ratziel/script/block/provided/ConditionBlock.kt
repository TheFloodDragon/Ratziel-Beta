package cn.fd.ratziel.script.block.provided

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.script.block.BlockParser
import cn.fd.ratziel.script.block.ExecutableBlock
import cn.fd.ratziel.script.block.RecursingBlockParser
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * ConditionBlock
 *
 * @author TheFloodDragon
 * @since 2024/10/2 17:42
 */
class ConditionBlock(
    val funcIf: ExecutableBlock,
    val funcThen: ExecutableBlock?,
    val funcElse: ExecutableBlock?
) : ExecutableBlock {

    override fun execute(context: ArgumentContext) =
        if (funcIf.execute(context) == true)
            funcThen?.execute(context)
        else funcElse?.execute(context)

    object Parser : RecursingBlockParser {

        override fun parse(element: JsonElement, parser: BlockParser): ConditionBlock? {
            if (element !is JsonObject) return null
            val valueIf = element["if"] ?: element["condition"] ?: return null
            val valueThen = element["then"]
            val valueElse = element["else"]
            return ConditionBlock(
                parser.parse(valueIf)!!,
                valueThen?.let { parser.parse(it) },
                valueElse?.let { parser.parse(it) }
            )
        }

    }

}