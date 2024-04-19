@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.core.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import java.util.function.Function

val baseJson by lazy {
    Json {
        // 宽松模式
        isLenient = true
        // 忽略未知键
        ignoreUnknownKeys = true
        // 隐式空值
        explicitNulls = false
        // 美观的打印方式
        prettyPrint = true
        // 枚举类不区分大小写
        decodeEnumsCaseInsensitive = true
        // 默认序列化模组
        serializersModule = baseSerializers
    }
}

/**
 * 编辑 [JsonObject]
 */
fun JsonObject.handle(action: HashMap<String, JsonElement>.() -> Unit): JsonObject = JsonObject(LinkedHashMap(this).apply(action))

/**
 * 从给定 [JsonElement] 中寻找 [JsonPrimitive]
 * 并通过 [action] 的操作后, 用返回的 [JsonElement] 替换掉原来的 [JsonPrimitive]
 */
fun JsonElement.handlePrimitives(element: JsonElement, action: Function<JsonPrimitive, JsonElement>): JsonElement =
    when (element) {
        is JsonPrimitive -> action.apply(element)
        is JsonArray -> buildJsonArray { element.jsonArray.forEach { add(handlePrimitives(it, action)) } }
        is JsonObject -> buildJsonObject {
            element.jsonObject.forEach { key, value ->
                put(key, handlePrimitives(value, action))
            }
        }
    }

fun JsonObject.getTentatively(vararg keys: String): JsonElement? = keys.firstNotNullOfOrNull { this[it] }

/**
 * 构造一个空Json如"{}"
 */
fun emptyJson() = JsonObject(emptyMap())

/**
 * 简单的Json检查
 */
fun String.isJson(): Boolean = startsWith('{') && endsWith('}')

/**
 * JsonPrimitive类型的判断
 */
fun JsonPrimitive.isInt() = !this.isString && this.intOrNull != null

fun JsonPrimitive.isLong() = !this.isString && this.longOrNull != null

fun JsonPrimitive.isBoolean() = !this.isString && this.booleanOrNull != null

fun JsonPrimitive.isDouble() = !this.isString && this.doubleOrNull != null

fun JsonPrimitive.isFloat() = !this.isString && this.floatOrNull != null