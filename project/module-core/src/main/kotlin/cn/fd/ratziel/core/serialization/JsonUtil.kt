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

fun JsonObject.getBy(names: Iterable<String>): JsonElement? {
    for (name in names) {
        val find = this[name]
        if (find != null) return find
    }
    return null
}

fun JsonObject.getBy(vararg names: String): JsonElement? {
    for (name in names) {
        val find = this[name]
        if (find != null) return find
    }
    return null
}

/**
 * 将 [JsonElement] 转为为纯原生对象形式
 * @see [JsonHelper.toBasic]
 */
inline fun JsonElement.toBasic(): Any = JsonHelper.toBasic(this)

/**
 * 映射 [JsonPrimitive]
 * @see [JsonHelper.mapPrimitives]
 */
inline fun JsonElement.mapPrimitives(action: Function<JsonPrimitive, JsonElement>): JsonElement = JsonHelper.mapPrimitives(this, action)

/**
 * 合并目标
 * @see [JsonHelper.merge]
 */
inline fun JsonObject.merge(target: JsonObject, replace: Boolean = true): JsonObject = JsonHelper.merge(this, target, replace)

/**
 * 简易的 [JsonObject] 检查
 */
fun String.isJsonObject(): Boolean = this.startsWith('{') && this.endsWith('}')