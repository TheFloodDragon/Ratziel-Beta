package cn.fd.ratziel.script.block

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/**
 * NamedBlockParser
 *
 * @author TheFloodDragon
 * @since 2024/10/3 15:13
 */
abstract class NamedBlockParser(
    /**
     * 动作名称
     */
    val names: Array<String>
) : BlockParser {

    /**
     * 通过文本解析
     * @param text 文本
     */
    abstract fun parse(text: String): ExecutableBlock?

    /**
     * 重写的 [parse]
     */
    override fun parse(element: JsonElement): ExecutableBlock? {
        if (element is JsonPrimitive && element.isString) {
            val text = checkStart(element.content) ?: return null
            return parse(text)
        } else return null
    }

    private fun checkStart(rawText: String): String? {
        for (name in names) {
            if (rawText.startsWith("$name:", ignoreCase = true)) {
                return name
            }
        }
        return null
    }

}