package cn.fd.ratziel.module.script.block

import cn.fd.ratziel.core.function.block.ExecutableBlock
import kotlinx.serialization.json.JsonElement

/**
 * RecursingBlockParser
 *
 * @author TheFloodDragon
 * @since 2024/10/3 15:31
 */
interface RecursingBlockParser : BlockParser {

    /**
     * 解析语句块
     * @param element 要解析的元素
     * @return 解析后的语句块, 若元素不符合要求, 则应返回为空
     */
    fun parse(element: JsonElement, parser: BlockParser): ExecutableBlock?

    /**
     * 禁用原来的方法
     */
    override fun parse(element: JsonElement) = throw UnsupportedOperationException("Please use RecursingBlockParser#parse(JsonElement, BlockBuilder)")

}