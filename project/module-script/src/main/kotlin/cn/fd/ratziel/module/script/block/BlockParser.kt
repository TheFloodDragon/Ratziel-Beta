package cn.fd.ratziel.module.script.block

import kotlinx.serialization.json.JsonElement

/**
 * BlockParser - 语句块解析器
 *
 * @author TheFloodDragon
 * @since 2024/10/2 17:36
 */
interface BlockParser {

    /**
     * 解析语句块
     * @param element 要解析的元素
     * @param context 构建时的上下文
     * @return 解析后的语句块, 若元素不符合要求, 则应返回为空
     */
    fun parse(element: JsonElement, context: BlockContext): ExecutableBlock?

}