package cn.fd.ratziel.core.serialization.serializers

import cn.fd.ratziel.core.util.ESCAPE_CHAR
import cn.fd.ratziel.core.util.removeAll
import cn.fd.ratziel.core.util.splitNonEscaped
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import java.util.*

/**
 * EnhancedListSerializer - 增强列表序列化器
 * 对Json进行转化,自动将转换换行符和删行符
 * 注意: 只接受字符串数组
 *
 * @author TheFloodDragon
 * @since 2023/10/5 14:45
 */
class EnhancedListSerializer<T : Any>(
    serializer: KSerializer<T>,
    // 换行符
    val newLineSign: Array<String> = DEFAULT_NEWLINE_SIGN,
    // 删行符
    val removeLineSign: Array<String> = DEFAULT_REMOVE_LINE_SIGN,
    // 忽略大小写
    val ignoreCase: Boolean = true,
) : JsonTransformingSerializer<List<T>>(ListSerializer(serializer)) {

    override fun transformDeserialize(element: JsonElement): JsonElement = JsonArray(enhanceBuild(element))

    /**
     * 构建增强列表
     */
    fun enhanceBuild(element: JsonElement, rawList: LinkedList<JsonElement> = LinkedList()): LinkedList<JsonElement> =
        rawList.also { list ->
            when {
                element is JsonArray -> element.forEach { enhanceBuild(it, list) }
                element is JsonPrimitive && element.jsonPrimitive.isString ->
                    element.content.splitNonEscaped(*newLineSign, ignoreCase = this.ignoreCase).forEach { origin ->
                        // (去掉 "\{rl}" 后的字符串) 是否包含任意删行符
                        val contain = removeLineSign.any {
                            origin.removeAll(ESCAPE_CHAR + it, ignoreCase).contains(it, ignoreCase)
                        }
                        // 若不包含,则添加进去
                        if (!contain) {
                            var result = origin
                            // 去除转义: 替换 "\{rl}" 为 "{rl}"
                            removeLineSign.forEach {
                                result = result.replace(ESCAPE_CHAR + it, it)
                            }
                            // 添加
                            list.add(JsonPrimitive(result))
                        }
                    }

                else -> list.add(element)
            }
        }

    companion object {

        // 默认换行符
        @JvmStatic
        val DEFAULT_NEWLINE_SIGN = arrayOf("\n", "{nl}")

        // 默认删行符
        @JvmStatic
        val DEFAULT_REMOVE_LINE_SIGN = arrayOf("{rl}", "{dl}")

    }

}