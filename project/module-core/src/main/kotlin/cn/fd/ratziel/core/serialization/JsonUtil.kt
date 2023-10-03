package cn.fd.ratziel.core.serialization

import kotlinx.serialization.json.*

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