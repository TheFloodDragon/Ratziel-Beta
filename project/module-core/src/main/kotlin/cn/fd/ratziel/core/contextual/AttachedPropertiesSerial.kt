package cn.fd.ratziel.core.contextual

import cn.fd.ratziel.core.serialization.json.baseJson
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject

/**
 * AttachedProperties 序列化键注册表
 */
object AttachedPropertiesSerialRegistry {

    private val keysByNodeName = LinkedHashMap<String, AttachedProperties.SerialKey<*>>()

    @Synchronized
    fun <T> register(key: AttachedProperties.SerialKey<T>): AttachedProperties.SerialKey<T> {
        registerNodeName(key.serialName, key)
        key.alias.forEach { registerNodeName(it, key) }
        @Suppress("UNCHECKED_CAST")
        return (keysByNodeName[key.serialName] ?: key) as AttachedProperties.SerialKey<T>
    }

    operator fun get(serialName: String): AttachedProperties.SerialKey<*>? = keysByNodeName[serialName]

    private fun registerNodeName(nodeName: String, key: AttachedProperties.SerialKey<*>) {
        val existing = keysByNodeName[nodeName]
        if (existing == null) {
            keysByNodeName[nodeName] = key
            return
        }
        require(isSameKey(existing, key)) {
            "Duplicate AttachedProperties serial node '$nodeName': existing=$existing, incoming=$key"
        }
    }

    private fun isSameKey(existing: AttachedProperties.SerialKey<*>, incoming: AttachedProperties.SerialKey<*>): Boolean {
        return existing.name == incoming.name &&
            existing.serialName == incoming.serialName &&
            existing.serializer.descriptor.serialName == incoming.serializer.descriptor.serialName
    }

}

/**
 * 将 [AttachedProperties] 序列化为 [JsonObject]
 */
fun AttachedProperties.serializeToJson(json: Json = baseJson): JsonObject =
    AttachedPropertiesKSerializer.serializeToJson(json, this)

/**
 * 将 [JsonElement] 反序列化为 [AttachedProperties]
 */
fun JsonElement.deserializeFromJson(json: Json = baseJson): AttachedProperties =
    AttachedPropertiesKSerializer.deserializeFromJson(json, this)

/**
 * 将 [AttachedProperties] 转换为 JSON 节点
 */
fun AttachedProperties.toJsonElement(json: Json = baseJson): JsonObject = serializeToJson(json)

/**
 * 将 JSON 节点转换为 [AttachedProperties]
 */
fun JsonElement.toAttachedProperties(json: Json = baseJson): AttachedProperties = deserializeFromJson(json)

/**
 * AttachedProperties 的 Kotlin 序列化器
 */
object AttachedPropertiesKSerializer : KSerializer<AttachedProperties> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("cn.fd.ratziel.core.contextual.AttachedProperties")

    override fun serialize(encoder: Encoder, value: AttachedProperties) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("AttachedPropertiesKSerializer only supports JsonEncoder")
        jsonEncoder.encodeJsonElement(value.serializeToJson(jsonEncoder.json))
    }

    override fun deserialize(decoder: Decoder): AttachedProperties {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("AttachedPropertiesKSerializer only supports JsonDecoder")
        return jsonDecoder.decodeJsonElement().deserializeFromJson(jsonDecoder.json)
    }

    fun serializeToJson(json: Json, value: AttachedProperties): JsonObject {
        val content = LinkedHashMap<String, JsonElement>()
        value.entries.forEach { (key, rawValue) ->
            val serialKey = key as? AttachedProperties.SerialKey<*> ?: return@forEach
            content[serialKey.serialName] = json.encodeSerialValue(serialKey, rawValue)
        }
        return JsonObject(content)
    }

    fun deserializeFromJson(json: Json, element: JsonElement): AttachedProperties {
        val jsonObject = element as? JsonObject
            ?: throw SerializationException("AttachedProperties must be deserialized from JsonObject, but was ${element::class.simpleName}")
        val mutable = AttachedProperties.Mutable()
        jsonObject.forEach { (serialName, jsonElement) ->
            val key = AttachedPropertiesSerialRegistry[serialName] ?: return@forEach
            mutable.putSerialValue(key, json, jsonElement)
        }
        return mutable.toImmutable()
    }

    @Suppress("UNCHECKED_CAST")
    private fun Json.encodeSerialValue(key: AttachedProperties.SerialKey<*>, value: Any?): JsonElement {
        key as AttachedProperties.SerialKey<Any?>
        return encodeToJsonElement(key.serializer, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun AttachedProperties.Mutable.putSerialValue(
        key: AttachedProperties.SerialKey<*>,
        json: Json,
        element: JsonElement,
    ) {
        key as AttachedProperties.SerialKey<Any?>
        this[key] = json.decodeFromJsonElement(key.serializer, element)
    }

}
