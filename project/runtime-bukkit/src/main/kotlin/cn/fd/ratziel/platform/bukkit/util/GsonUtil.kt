package cn.fd.ratziel.platform.bukkit.util

import kotlinx.serialization.json.*

/**
 * GsonUtil
 * 
 * @author TheFloodDragon
 * @since 2025/11/29 20:58
 */

typealias GsonJsonElement = com.google.gson.JsonElement
typealias GsonJsonPrimitive = com.google.gson.JsonPrimitive
typealias GsonJsonNull = com.google.gson.JsonNull
typealias GsonJsonObject = com.google.gson.JsonObject
typealias GsonJsonArray = com.google.gson.JsonArray

fun GsonJsonElement.toKotlinx(): JsonElement = when (this) {
    is GsonJsonNull -> JsonNull
    is GsonJsonPrimitive -> JsonPrimitive(this.asString)
    is GsonJsonObject -> JsonObject(this.entrySet().associate { it.key to it.value.toKotlinx() })
    is GsonJsonArray -> JsonArray(this.map { it.toKotlinx() })
    else -> error("Unsupported Gson JsonElement: $this")
}

fun JsonElement.toGson(): GsonJsonElement = when (val el = this) {
    is JsonNull -> GsonJsonNull.INSTANCE
    is JsonPrimitive -> {
        // Preserve quoted strings
        if (el.isString) return GsonJsonPrimitive(el.content)

        // Try to convert to boolean / integer / double in that order, fall back to string
        el.booleanOrNull?.let { return GsonJsonPrimitive(it) }
        el.longOrNull?.let { return GsonJsonPrimitive(it) }
        el.doubleOrNull?.let { return GsonJsonPrimitive(it) }

        // Fallback: treat as string
        GsonJsonPrimitive(el.content)
    }
    is JsonObject -> GsonJsonObject().apply {
        for ((k, v) in el) add(k, v.toGson())
    }
    is JsonArray -> GsonJsonArray().apply {
        for (item in el) add(item.toGson())
    }
}
