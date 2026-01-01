package cn.fd.ratziel.module.item.api.component.transformer

import cn.fd.ratziel.core.util.getBy
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * SerialJsonTransformer
 * 
 * @author TheFloodDragon
 * @since 2026/1/1 22:12
 */
open class SerialJsonTransformer<T>(
    val serializer: KSerializer<T>,
    val jsonFormat: Json,
) : ItemComponentType.JsonTransformer<T> {

    override fun transformToJson(tar: T): JsonElement {
        return jsonFormat.encodeToJsonElement(serializer, tar)
    }

    override fun detransformFromJson(src: JsonElement): T? {
        return jsonFormat.decodeFromJsonElement(serializer, src)
    }

    override fun toString() = "SerialJsonTransformer(serializer=$serializer, jsonFormat=$jsonFormat)"

    /**
     * EntryTransformer
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

        override fun transformToJson(tar: T): JsonObject {
            val serialized = super.transformToJson(tar)
            return JsonObject(mapOf(serialName to serialized))
        }

        override fun detransformFromJson(src: JsonElement): T? {
            if (src !is JsonObject) return null // 仅支持对象
            val element = src[serialName] ?: src.getBy(*alias) ?: return null
            return super.detransformFromJson(element)
        }

        override fun toString() = "SerialJsonEntryTransformer(serialName=$serialName, alias=$alias, serializer=$serializer, jsonFormat=$jsonFormat)"

    }

}