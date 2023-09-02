package cn.fd.ratziel.core.serialize

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