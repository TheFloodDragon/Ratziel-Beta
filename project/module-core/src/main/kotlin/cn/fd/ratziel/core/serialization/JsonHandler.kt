package cn.fd.ratziel.core.serialization

import kotlinx.serialization.json.*
import java.util.function.Function

/**
 * JsonHandler
 *
 * @author TheFloodDragon
 * @since 2024/5/2 12:56
 */
object JsonHandler {

    /**
     * 合并目标
     * @param replace 是否替换原有的
     */
    fun merge(source: JsonObject, target: JsonObject, replace: Boolean = true): MutableJsonObject = source.asMutable().also { map ->
        target.forEach { (key, targetValue) ->
            val ownValue = map[key]
            // 如果当前中存在, 且不允许替换, 则直接跳出循环
            if (ownValue != null && !replace) return@forEach
            // 反则设置值 (复合类型时递归)
            if (targetValue is JsonObject) {
                // 判断当前值类型 (若非复合类型,则替换,此时目标值是复合类型的)
                val value: JsonObject? = ownValue as? JsonObject
                map[key] = JsonObject(merge((value ?: emptyJson()), targetValue, replace))
            } else map[key] = targetValue
        }
    }

    /**
     * 从给定 [JsonElement] 中寻找 [JsonPrimitive]
     * 并通过 [action] 的操作后, 用返回的 [JsonElement] 替换掉原来的 [JsonPrimitive]
     */
    fun handlePrimitives(element: JsonElement, action: Function<JsonPrimitive, JsonElement>): JsonElement =
        when (element) {
            is JsonPrimitive -> action.apply(element)
            is JsonArray -> JsonArray(element.map { handlePrimitives(it, action) })
            is JsonObject -> buildJsonObject {
                element.forEach { put(it.key, handlePrimitives(it.value, action)) }
            }
        }

}