@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.core.contextual

import cn.fd.ratziel.core.serialization.json.baseJson
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


/**
 * 创建可序列化属性键。
 *
 * 建议让 [group] 与声明该属性的 Kotlin 文件保持一致，便于查找与维护注册关系。
 */
fun <T> AttachedProperties.Companion.serialKey(group: SerialGroup, serializer: KSerializer<T>, defaultValue: T) =
    PropertySerialKeyDelegate(group, serializer) { defaultValue }

fun <T> AttachedProperties.Companion.serialKey(
    group: SerialGroup,
    serializer: KSerializer<T>,
    getDefaultValue: AttachedProperties.() -> T,
) = PropertySerialKeyDelegate(group, serializer, getDefaultValue)

inline fun <reified T> AttachedProperties.Companion.serialKey(group: SerialGroup, defaultValue: T) =
    serialKey(group, serializer<T>(), defaultValue)

inline fun <reified T> AttachedProperties.Companion.serialKey(
    group: SerialGroup,
    noinline getDefaultValue: AttachedProperties.() -> T,
) = serialKey(group, serializer<T>(), getDefaultValue)

/**
 * 将 [AttachedProperties] 序列化为 [JsonObject]
 */
fun AttachedProperties.serializeToJson(
    group: SerialGroup,
    json: Json = baseJson,
): JsonObject = group.typedSerializer().serializeToJson(json, this)

/**
 * 将 [JsonElement] 反序列化为 [AttachedProperties]
 */
fun JsonElement.deserializeFromJson(
    group: SerialGroup,
    json: Json = baseJson,
): AttachedProperties = group.typedSerializer().deserializeFromJson(json, this)

/**
 * 将 [AttachedProperties] 转换为 JSON 节点
 */
fun AttachedProperties.toJsonElement(
    group: SerialGroup,
    json: Json = baseJson,
): JsonObject = serializeToJson(group, json)

/**
 * 将 JSON 节点转换为 [AttachedProperties]
 */
fun JsonElement.toAttachedProperties(
    group: SerialGroup,
    json: Json = baseJson,
): AttachedProperties = deserializeFromJson(group, json)


/**
 * 序列化分组。
 *
 * 建议将分组对象与对应的 [SerialKey] 属性放在同一个 Kotlin 文件中维护，
 * 以避免后续重构时遗漏注册定义。
 */
open class SerialGroup {

    private val keysByNodeName = LinkedHashMap<String, SerialKey<*>>()
    private val attachedPropertiesSerializer by lazy(LazyThreadSafetyMode.PUBLICATION) { AttachedPropertiesKSerializer(this) }

    @Synchronized
    fun <T> register(key: SerialKey<T>): SerialKey<T> {
        require(key.group === this) {
            "SerialKey $key belongs to ${key.group}, but was registered into $this"
        }
        registerNodeName(key.serialName, key)
        key.alias.forEach { registerNodeName(it, key) }
        @Suppress("UNCHECKED_CAST")
        return (keysByNodeName[key.serialName] ?: key) as SerialKey<T>
    }

    operator fun get(serialName: String): SerialKey<*>? = keysByNodeName[serialName]

    fun serializer(): KSerializer<AttachedProperties> = attachedPropertiesSerializer

    internal fun typedSerializer(): AttachedPropertiesKSerializer = attachedPropertiesSerializer

    override fun toString(): String = javaClass.simpleName

    private fun registerNodeName(nodeName: String, key: SerialKey<*>) {
        val existing = keysByNodeName[nodeName]
        if (existing == null) {
            keysByNodeName[nodeName] = key
            return
        }
        require(isSameKey(existing, key)) {
            "Duplicate AttachedProperties serial node '$nodeName' in $this: existing=$existing, incoming=$key"
        }
    }

    private fun isSameKey(existing: SerialKey<*>, incoming: SerialKey<*>): Boolean {
        return existing.group === incoming.group &&
                existing.name == incoming.name &&
                existing.serialName == incoming.serialName &&
                existing.alias == incoming.alias &&
                existing.serializer.descriptor.serialName == incoming.serializer.descriptor.serialName
    }

}

/**
 * 可序列化的属性键
 */
open class SerialKey<T>(
    name: String,
    /** 所属分组 **/
    val group: SerialGroup,
    /** 序列化名称 **/
    val serialName: String,
    /** 反序列化别名 **/
    val alias: Set<String> = emptySet(),
    /** 序列化器 **/
    val serializer: KSerializer<T>,
    getDefaultValue: AttachedProperties.() -> T,
) : AttachedProperties.Key<T>(name, getDefaultValue) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is SerialKey<*> && other.group === group && other.name == name
    }

    override fun hashCode() = 31 * System.identityHashCode(group) + name.hashCode()

    override fun toString() = "SerialKey($name@$group:$serialName, aliases=$alias)"
}

class PropertySerialKeyDelegate<T>(
    private val group: SerialGroup,
    private val serializer: KSerializer<T>,
    private val getDefaultValue: AttachedProperties.() -> T,
) : ReadOnlyProperty<Any?, SerialKey<T>> {

    private lateinit var key: SerialKey<T>

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): PropertySerialKeyDelegate<T> {
        key = group.register(createKey(property))
        return this
    }

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): SerialKey<T> = key

    private fun createKey(property: KProperty<*>) = SerialKey(
        name = property.name,
        group = group,
        serialName = property.annotations.filterIsInstance<SerialName>().firstOrNull()?.value ?: property.name,
        alias = property.annotations
            .filterIsInstance<JsonNames>()
            .flatMapTo(LinkedHashSet()) { it.names.asIterable() },
        serializer = serializer,
        getDefaultValue = getDefaultValue,
    )
}

/**
 * AttachedProperties 的 Kotlin 序列化器
 */
class AttachedPropertiesKSerializer(private val group: SerialGroup) : KSerializer<AttachedProperties> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("cn.fd.ratziel.core.contextual.AttachedProperties")

    override fun serialize(encoder: Encoder, value: AttachedProperties) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("AttachedPropertiesKSerializer only supports JsonEncoder")
        jsonEncoder.encodeJsonElement(serializeToJson(jsonEncoder.json, value))
    }

    override fun deserialize(decoder: Decoder): AttachedProperties {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("AttachedPropertiesKSerializer only supports JsonDecoder")
        return deserializeFromJson(jsonDecoder.json, jsonDecoder.decodeJsonElement())
    }

    fun serializeToJson(json: Json, value: AttachedProperties): JsonObject {
        val content = LinkedHashMap<String, JsonElement>()
        value.entries.forEach { (key, rawValue) ->
            val serialKey = key as? SerialKey<*> ?: return@forEach
            if (serialKey.group !== group) return@forEach
            content[serialKey.serialName] = json.encodeSerialValue(serialKey, rawValue)
        }
        return JsonObject(content)
    }

    fun deserializeFromJson(json: Json, element: JsonElement): AttachedProperties {
        val jsonObject = element as? JsonObject
            ?: throw SerializationException("AttachedProperties must be deserialized from JsonObject, but was ${element::class.simpleName}")
        val mutable = AttachedProperties.Mutable()
        jsonObject.forEach { (serialName, jsonElement) ->
            val key = group[serialName] ?: return@forEach
            mutable.putSerialValue(key, json, jsonElement)
        }
        return mutable.toImmutable()
    }

    @Suppress("UNCHECKED_CAST")
    private fun Json.encodeSerialValue(key: SerialKey<*>, value: Any?): JsonElement {
        key as SerialKey<Any?>
        return encodeToJsonElement(key.serializer, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun AttachedProperties.Mutable.putSerialValue(
        key: SerialKey<*>,
        json: Json,
        element: JsonElement,
    ) {
        key as SerialKey<Any?>
        this[key] = json.decodeFromJsonElement(key.serializer, element)
    }

}
