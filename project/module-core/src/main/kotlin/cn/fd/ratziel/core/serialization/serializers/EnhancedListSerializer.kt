package cn.fd.ratziel.core.serialization.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

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
    fun enhanceBuild(element: JsonElement, list: MutableList<JsonElement> = mutableListOf()): MutableList<JsonElement> {
        if (element is JsonArray) {
            element.forEach {
                enhanceBuild(it, list)
            }
        } else if (element is JsonPrimitive && element.jsonPrimitive.isString) {
            element.content.split(NEWLINE_SIGN)
                .forEach {
                    if (!it.contains(REMOVE_LINE_SIGN)) list.add(JsonPrimitive(it))
                }
        } else list.add(element)
        return list
    }

}

// 换行符
const val NEWLINE_SIGN = '\n'

// 删行符
const val REMOVE_LINE_SIGN = "<{dl}>"