package cn.fd.ratziel.core.serialization

import kotlinx.serialization.json.*
import java.util.function.Function

/**
 * JsonHandler
 * Json工具, 用于简易地操作 [JsonElement]
 *
 * @author TheFloodDragon
 * @since 2024/2/10 17:07
 */
object JsonHandler {

    /**
     * 编辑 [JsonObject]
     */
    @JvmStatic
    fun edit(json: JsonObject, action: HashMap<String, JsonElement>.() -> Unit): JsonObject = JsonObject(LinkedHashMap(json).apply(action))

    /**
     * 从给定 [JsonElement] 中寻找 [JsonPrimitive]
     * 并通过 [action] 的操作后, 用返回的 [JsonElement] 替换掉原来的 [JsonPrimitive]
     */
    @JvmStatic
    fun unfold(element: JsonElement, action: Function<JsonPrimitive, JsonElement>): JsonElement =
        when (element) {
            is JsonPrimitive -> action.apply(element)
            is JsonArray -> buildJsonArray { element.jsonArray.forEach { add(unfold(it, action)) } }
            is JsonObject -> buildJsonObject {
                element.jsonObject.forEach { key, value ->
                    put(key, unfold(value, action))
                }
            }
        }

}