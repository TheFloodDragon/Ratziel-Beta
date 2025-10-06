package cn.fd.ratziel.common.block.provided

import cn.fd.ratziel.common.block.BlockContext
import cn.fd.ratziel.common.block.BlockParser
import cn.fd.ratziel.common.block.ExecutableBlock
import cn.fd.ratziel.common.util.varsMap
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.util.adapt
import cn.fd.ratziel.core.util.getBy
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/**
 * WhenBlock
 *
 * @author TheFloodDragon
 * @since 2025/10/06
 */
class WhenBlock(
    private val input: String,
    private val branches: Map<ExecutableBlock, ExecutableBlock>,
    private val default: ExecutableBlock?,
) : ExecutableBlock {

    override fun execute(context: ArgumentContext): Any? {
        val inputValue = context.varsMap()[input] // 从变量表中获取
        for ((case, value) in branches) {
            if (case.execute(context) == inputValue) {
                return value.execute(context)
            }
        }
        return default?.execute(context)
    }

    object Parser : BlockParser {

        @JvmStatic
        private val DEFAULT_STATEMENT = arrayOf("else", "default", "def")

        override fun parse(element: JsonElement, context: BlockContext): ExecutableBlock? {
            if (element !is JsonObject || element.size != 1) return null

            val entry = element.entries.first()
            val key = entry.key
            if (!entry.key.startsWith("when")) return null

            // 输入量
            val input = key.substringAfter("when")
                .trim { it in arrayOf('\'', '"', ' ') }

            // 分支解析
            val branches = when (val value = entry.value) {
                is JsonObject -> parseMapBranches(value, context)
                is JsonArray -> parseListBranches(value, context)
                else -> error("Unexpected branch structure for when statement: $value")
            }

            return WhenBlock(input, branches.first, branches.second)
        }

        @JvmStatic
        private fun parseMapBranches(items: JsonObject, context: BlockContext): Pair<Map<ExecutableBlock, ExecutableBlock>, ExecutableBlock?> {
            val branches = hashMapOf<ExecutableBlock, ExecutableBlock>()
            var default: ExecutableBlock? = null
            for ((caseKey, valueElement) in items) {
                // 默认值
                if (caseKey in DEFAULT_STATEMENT) {
                    default = context.parse(valueElement)
                } else {
                    branches[ValueBlock(caseKey.adapt())] = context.parse(valueElement)
                }
            }
            return branches to default
        }

        @JvmStatic
        private fun parseListBranches(items: JsonArray, context: BlockContext): Pair<Map<ExecutableBlock, ExecutableBlock>, ExecutableBlock?> {
            val branches = hashMapOf<ExecutableBlock, ExecutableBlock>()
            var default: ExecutableBlock? = null
            for (branch in items) {
                val branch = branch.jsonObject
                // 默认值
                if (branch.entries.firstOrNull()?.key in DEFAULT_STATEMENT) {
                    default = context.parse(branch.entries.first().value)
                } else {
                    val caseElement = requireNotNull(branch["case"]) { "No case statement." }
                    val valueElement = requireNotNull(branch.getBy("yield", "then", "value", "return")) { "No yield statement." }
                    branches[context.parse(caseElement)] = context.parse(valueElement)
                }
            }
            return branches to default
        }
    }

}