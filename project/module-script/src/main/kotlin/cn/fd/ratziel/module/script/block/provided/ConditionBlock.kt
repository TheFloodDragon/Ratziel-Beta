package cn.fd.ratziel.module.script.block.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.script.block.BlockParser
import cn.fd.ratziel.module.script.block.ExecutableBlock
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * ConditionBlock
 *
 * @author TheFloodDragon
 * @since 2025/3/23 09:16
 */
class ConditionBlock(
    /** 条件语句 (必选) **/
    val funcIf: ExecutableBlock,
    val funcThen: ExecutableBlock?,
    val funcElse: ExecutableBlock?,
) : ExecutableBlock {

    override fun execute(context: ArgumentContext) {
        if (funcIf.execute(context) == true)
            funcThen?.execute(context)
        else funcElse?.execute(context)
    }

    object Parser : BlockParser {
        override fun parse(element: JsonElement, scheduler: BlockParser): ConditionBlock? {
            if (element !is JsonObject) return null
            val valueIf = element["if"] ?: element["condition"] ?: return null
            val valueThen = element["then"]
            val valueElse = element["else"]
            return ConditionBlock(
                scheduler.parse(valueIf, scheduler)!!,
                valueThen?.let { scheduler.parse(it, scheduler) },
                valueElse?.let { scheduler.parse(it, scheduler) }
            )
        }
    }

}