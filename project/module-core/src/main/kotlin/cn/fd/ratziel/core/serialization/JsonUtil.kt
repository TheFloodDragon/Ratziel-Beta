package cn.fd.ratziel.core.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*

@OptIn(ExperimentalSerializationApi::class)
val baseJson = Json {
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
}

/**
 * JsonElement自适应
 */
fun JsonElement.adapt(): Any {
    return when (this) {
        is JsonObject -> this.jsonObject
        is JsonArray -> this.jsonArray
        is JsonPrimitive -> this.jsonPrimitive
        else -> this.jsonNull
    }
}

/**
 * 构造一个空Json如"{}"
 */
fun emptyJson() = JsonObject(emptyMap())