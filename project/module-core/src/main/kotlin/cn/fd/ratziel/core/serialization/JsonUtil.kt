@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.core.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.function.Function

val baseJson by lazy {
    Json {
        // 宽松模式
        isLenient = true
        // 忽略未知键
        ignoreUnknownKeys = true
        // 隐式空值
        explicitNulls = false
        // 数据修正
        coerceInputValues = true
        // 美观的打印方式
        prettyPrint = true
        // 枚举类不区分大小写
        decodeEnumsCaseInsensitive = true
    }
}

operator fun JsonObject.get(names: Iterable<String>): JsonElement? {
    for (name in names) {
        val find = this[name]
        if (find != null) return find
    }
    return null
}

/**
 * 可变化
 */
fun Map<String, JsonElement>.asMutable(): MutableJsonObject =
    when (this) {
        is MutableJsonObject -> this
        is MutableMap<*, *> -> MutableJsonObject(this as MutableMap<String, JsonElement>)
        else -> MutableJsonObject(this.toMutableMap())
    }

/**
 * 处理 [JsonObject]
 * @action 处理动作, 会接受原来的 [JsonObject]
 * @return 一个新的 [JsonObject]
 */
inline fun JsonObject.handle(action: MutableJsonObject.(JsonObject) -> Unit): JsonObject = JsonObject(this.asMutable().also { action(it, this) })

/**
 * 处理[JsonPrimitive]
 * @see [JsonHandler.handlePrimitives]
 */
inline fun JsonElement.handlePrimitives(action: Function<JsonPrimitive, JsonElement>): JsonElement = JsonHandler.handlePrimitives(this, action)

/**
 * 合并目标
 * @see [JsonHandler.merge]
 */
inline fun JsonObject.merge(target: JsonObject, replace: Boolean = true): JsonObject = JsonHandler.merge(this.asMutable(), target, replace).asImmutable()

/**
 * 简易的 [JsonObject] 检查
 */
fun String.isJsonObject(): Boolean = this.startsWith('{') && this.endsWith('}')