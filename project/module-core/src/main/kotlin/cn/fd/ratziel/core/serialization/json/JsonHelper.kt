package cn.fd.ratziel.core.serialization.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.function.Function

/**
 * JsonHelper
 *
 * @author TheFloodDragon
 * @since 2024/5/2 12:56
 */
object JsonHelper {

    /**
     * 将 [JsonElement] 转为为纯原生对象形式
     */
    fun toBasic(element: JsonElement): Any = when (element) {
        is JsonObject -> buildMap { element.forEach { put(it.key, toBasic(it.value)) } }
        is JsonArray -> element.map { toBasic(it) }
        is JsonPrimitive -> element.content
    }

    /**
     * 合并目标
     * @param replace 是否替换原有的
     */
    fun merge(source: JsonObject, target: JsonObject, replace: Boolean = true): JsonObject {
        val map = source.toMutableMap()
        for ((key, targetValue) in target) {
            // 获取自身的数据
            val ownValue = map[key]
            // 如果自身数据不存在, 或者允许替换, 则直接替换, 反则跳出循环
            map[key] = when (targetValue) {
                // 目标值为 Compound 类型
                is JsonObject -> (ownValue as? JsonObject)
                    ?.let { merge(it, targetValue, replace) } // 同类型合并
                // 目标值为基础类型
                else -> null
            } ?: if (ownValue == null || replace) targetValue else continue
        }
        return JsonObject(map)
    }

    /**
     * 映射 [JsonPrimitive]
     */
    fun mapPrimitives(element: JsonElement, action: Function<JsonPrimitive, JsonElement>): JsonElement = when (element) {
        is JsonPrimitive -> action.apply(element)
        is JsonArray -> JsonArray(element.map { mapPrimitives(it, action) })
        is JsonObject -> JsonObject(element.mapValues { mapPrimitives(it.value, action) })
    }

}