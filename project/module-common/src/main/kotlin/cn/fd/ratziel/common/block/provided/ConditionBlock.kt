package cn.fd.ratziel.common.block.provided

import cn.fd.ratziel.common.block.BlockContext
import cn.fd.ratziel.common.block.BlockParser
import cn.fd.ratziel.common.block.ExecutableBlock
import cn.fd.ratziel.core.contextual.ArgumentContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * ConditionBlock - 条件语句块
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

    /**
     * AnyBlock
     */
    class AnyBlock(
        val blocks: List<ExecutableBlock>,
    ) : ExecutableBlock {
        override fun execute(context: ArgumentContext) = blocks.any { it.execute(context) == true }
    }

    /**
     * AllBlock
     */
    class AllBlock(
        val blocks: List<ExecutableBlock>,
    ) : ExecutableBlock {
        override fun execute(context: ArgumentContext) = blocks.all { it.execute(context) == true }
    }

    object Parser : BlockParser {
        override fun parse(element: JsonElement, context: BlockContext): ExecutableBlock? {
            // 需要对象类型, 并且不允许有别的内容 (仅 if、then、else 多了就不会被解析为条件语句块)
            if (element !is JsonObject || element.size > 3) return null

            // 尝试解析条件语句
            val valueIf = element["if"] ?: element["condition"]
            if (valueIf != null) {
                val valueThen = element["then"]
                val valueElse = element["else"]
                return ConditionBlock(
                    context.parse(valueIf),
                    valueThen?.let { context.parse(it) },
                    valueElse?.let { context.parse(it) }
                )
            }

            // 尝试解析 any、all 语句
            if (element.size == 1) {
                val valueAny = element["any"]
                if (valueAny != null && valueAny is JsonArray) {
                    return AnyBlock(
                        valueAny.map { context.parse(it) }
                    )
                }
                val valueAll = element["all"]
                if (valueAll != null && valueAll is JsonArray) {
                    return AllBlock(
                        valueAll.map { context.parse(it) }
                    )
                }
            }

            return null
        }
    }

}