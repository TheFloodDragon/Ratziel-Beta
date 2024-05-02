@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("NOTHING_TO_INLINE")

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
 * 可变的 [JsonObject]
 */
typealias MutableJsonObject = MutableMap<String, JsonElement>

/**
 * 可变化
 */
inline fun Map<String, JsonElement>.asMutable(): MutableJsonObject = LinkedHashMap(this)

/**
 * 处理 [JsonObject]
 * @action 处理动作, 会接受原来的 [JsonObject]
 * @return 一个新的 [JsonObject]
 */
fun JsonObject.handle(action: MutableJsonObject.(JsonObject) -> Unit): JsonObject = JsonObject(this.asMutable().also { action(it, this) })

/**
 * 处理[JsonPrimitive]
 * @see [JsonHandler.handlePrimitives]
 */
fun JsonElement.handlePrimitives(action: Function<JsonPrimitive, JsonElement>): JsonElement = JsonHandler.handlePrimitives(this, action)

/**
 * 合并目标
 * @see [JsonHandler.merge]
 */
fun JsonObject.merge(target: JsonObject, replace: Boolean = true): MutableJsonObject = JsonHandler.merge(this, target, replace)

/**
 * 构造一个空的[JsonObject]
 */
inline fun emptyJson() = JsonObject(emptyMap())

/**
 * 简易的[JsonObject]检查
 */
fun String.isJsonObject(): Boolean = startsWith('{') && endsWith('}')

/**
 * [JsonPrimitive]类型的判断
 */
fun JsonPrimitive.isInt() = !this.isString && this.intOrNull != null

fun JsonPrimitive.isLong() = !this.isString && this.longOrNull != null

fun JsonPrimitive.isBoolean() = !this.isString && this.booleanOrNull != null

fun JsonPrimitive.isDouble() = !this.isString && this.doubleOrNull != null

fun JsonPrimitive.isFloat() = !this.isString && this.floatOrNull != null