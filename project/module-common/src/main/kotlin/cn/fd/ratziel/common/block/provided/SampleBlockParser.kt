package cn.fd.ratziel.common.block.provided

import cn.fd.ratziel.common.block.BlockContext
import cn.fd.ratziel.common.block.BlockParser
import cn.fd.ratziel.common.block.ExecutableBlock
import cn.fd.ratziel.core.contextual.ArgumentContext
import kotlinx.serialization.json.*
import kotlin.random.Random

/**
 * SampleBlockParser
 *
 * @author TheFloodDragon
 * @since 2025/10/5 18:46
 */
object SampleBlockParser : BlockParser {

    /**
     * 加权随机采样
     */
    class WeightedSampleBlock(items: List<Pair<Double, ExecutableBlock>>) : ExecutableBlock {

        // 类加权重
        private val cumulativeWeights = items.runningFold(0.0) { acc, pair -> acc + pair.first }.toDoubleArray()

        // 所有值
        private val values = items.map { it.second }.toTypedArray()

        override fun execute(context: ArgumentContext): Any? {
            // 上限总权值, 取一个随机数
            val ran = Random.nextDouble(cumulativeWeights.last())
            // 寻找落中区间
            for (i in 0..cumulativeWeights.lastIndex) {
                if (cumulativeWeights[i] > ran) {
                    return values[i].execute(context)
                }
            }
            error("No reachable.")
        }

    }

    /**
     * 均匀随机
     */
    class RandomSampleBlock(val items: List<ExecutableBlock>) : ExecutableBlock {
        override fun execute(context: ArgumentContext) = items.randomOrNull()?.execute(context)
    }

    /**
     * 区间采样
     */
    /**
     * 解析
     */
    override fun parse(element: JsonElement, context: BlockContext): ExecutableBlock? {
        // 只处理 sample 相关结构
        if (element !is JsonObject || element.size != 1) return null
        val entry = element.entries.first()
        val key = entry.key.takeIf { it.contains("sample") }
            ?.filterNot { it.isWhitespace() } ?: return null
        val items = entry.value.jsonArray

        // 随机加权采样
        if (key.contains("@weight")) {
            return WeightedSampleBlock(items.map { parseWeighted(it, context) })
        }

        // 等概率采样
        return RandomSampleBlock(items.map { context.parse(it) })
    }

    private fun parseWeighted(element: JsonElement, context: BlockContext): Pair<Double, ExecutableBlock> {
        val valueElement = requireNotNull(element.jsonObject["value"]) { "No value." }
        val weight = requireNotNull(element.jsonObject["weight"]?.jsonPrimitive?.double) { "No weight." }
        require(weight >= 0) { "Weight must be non-negative." }
        return weight to context.parse(valueElement)
    }

}