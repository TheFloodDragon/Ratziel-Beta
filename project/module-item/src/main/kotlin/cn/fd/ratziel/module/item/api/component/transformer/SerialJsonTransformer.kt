package cn.fd.ratziel.module.item.api.component.transformer

import cn.fd.ratziel.core.util.getBy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * SerialJsonTransformer
 *
 * 基于 Kotlinx Serialization 的 [JsonTransformer] 默认实现。
 *
 * @author TheFloodDragon
 * @since 2026/1/1 22:12
 */
open class SerialJsonTransformer<T>(
    val serializer: KSerializer<T>,
    val jsonFormat: Json,
) : JsonTransformer<T> {

    override fun toJsonElement(component: T): JsonElement {
        return jsonFormat.encodeToJsonElement(serializer, component)
    }

    override fun fromJsonElement(element: JsonElement): T? {
        return jsonFormat.decodeFromJsonElement(serializer, element)
    }

    override fun toString() = "SerialJsonTransformer(serializer=$serializer)"

    /**
     * EntryTransformer
     *
     * 将组件映射到 JSON 对象中的指定主字段，并支持别名读取。
     *
     * @author TheFloodDragon
     * @since 2026/1/1 21:41
     */
    open class EntryTransformer<T>(
        serializer: KSerializer<T>,
        jsonFormat: Json,
        /** 主名称 (序列化名) **/
        val serialName: String,
        /** 别名 (识别名) **/
        vararg val alias: String,
    ) : SerialJsonTransformer<T>(serializer, jsonFormat) {

        override fun toJsonElement(component: T): JsonObject {
            val serialized = super.toJsonElement(component)
            return JsonObject(mapOf(serialName to serialized))
        }

        override fun fromJsonElement(element: JsonElement): T? {
            if (element !is JsonObject) return null // 仅支持对象
            val element = element[serialName] ?: element.getBy(*alias) ?: return null
            return super.fromJsonElement(element)
        }

        override fun toString() = "SerialJsonEntryTransformer(serialName='$serialName', alias=${alias.contentToString()})"

    }

}