package cn.fd.ratziel.script.block.provided

import cn.fd.ratziel.script.api.ScriptEnvironment
import cn.fd.ratziel.script.block.BlockManager
import cn.fd.ratziel.script.block.BlockParser
import cn.fd.ratziel.script.block.ExecutableBlock
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

    override fun execute(environment: ScriptEnvironment) =
        if (funcIf.execute(environment) == true)
            funcThen?.execute(environment)
        else funcElse?.execute(environment)

    object Parser : BlockParser {

        override fun parse(element: JsonElement): ConditionBlock? {
            if (element !is JsonObject) return null
            val valueIf = element["if"] ?: element["condition"] ?: return null
            val valueThen = element["then"]
            val valueElse = element["else"]
            return ConditionBlock(
                BlockManager.parse(valueIf),
                BlockManager.parse(valueThen),
                BlockManager.parse(valueElse)
            )
        }

    }

}