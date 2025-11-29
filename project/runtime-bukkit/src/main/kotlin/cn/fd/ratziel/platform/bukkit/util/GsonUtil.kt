package cn.fd.ratziel.platform.bukkit.util

import com.google.gson.JsonParser
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

fun JsonElement.toGson(): GsonJsonElement = JsonParser().parse(this.toString())
