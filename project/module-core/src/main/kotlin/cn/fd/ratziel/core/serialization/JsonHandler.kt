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
    fun merge(source: MutableJsonObject, target: JsonObject, replace: Boolean = true): MutableJsonObject = source.also { map ->
        target.forEach { (key, targetValue) ->
            // 获取自身的数据
            val ownValue = map[key]
            // 如果自身数据不存在, 或者允许替换, 则直接替换, 反则跳出循环
            map[key] = when (targetValue) {
                // 目标值为 Compound 类型
                is JsonObject -> (ownValue as? JsonObject)?.asMutable()
                    ?.let { merge(it, targetValue, replace) }?.asImmutable() // 同类型合并
                // 目标值为基础类型
                else -> null
            } ?: if (ownValue == null || replace) targetValue else return@forEach
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