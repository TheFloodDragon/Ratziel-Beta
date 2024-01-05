package cn.fd.ratziel.core.serialization.serializers

import cn.fd.ratziel.core.util.runIfContainsNonEscaped
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
class EnhancedListSerializer<T : Any>(serializer: KSerializer<T>) :
    JsonTransformingSerializer<List<T>>(ListSerializer(serializer)) {

    override fun transformDeserialize(element: JsonElement): JsonElement = JsonArray(enhanceBuild(element))

    /**
     * 构建增强列表
     */
    fun enhanceBuild(element: JsonElement, rawList: LinkedList<JsonElement> = LinkedList()): LinkedList<JsonElement> =
        rawList.also { list ->
            when {
                element is JsonArray -> element.forEach { enhanceBuild(it, list) }
                element is JsonPrimitive && element.jsonPrimitive.isString ->
                    element.content.split(NEWLINE_SIGN).forEach {
                        it.runIfContainsNonEscaped(REMOVE_LINE_SIGN) { s ->
                            list.add(JsonPrimitive(s))
                        }
                    }

                else -> list.add(element)
            }
        }

}

// 换行符
const val NEWLINE_SIGN = '\n'

// 删行符
const val REMOVE_LINE_SIGN = "{dl}"