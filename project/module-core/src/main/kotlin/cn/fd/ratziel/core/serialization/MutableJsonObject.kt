package cn.fd.ratziel.core.serialization

import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import taboolib.library.reflex.ReflexClass

/**
 * MutableJsonObject - 可变的 [JsonObject]
 *
 * @author TheFloodDragon
 * @since 2024/5/22 22:09
 */
@Serializable(MutableJsonObject.MutableJsonObjectSerializer::class)
open class MutableJsonObject(
    val content: MutableMap<String, JsonElement>
) : MutableMap<String, JsonElement> by content {

    constructor() : this(ConcurrentHashMap())

    /**
     * [MutableJsonObject] 存储的不可变 [JsonObject] 实例
     * 其 [JsonObject.content] 应该与 [MutableJsonObject.content] 是同一个对象 (内存地址相同)
     */
    protected open val delegate: JsonObject = JsonObject(this.content)

    /**
     * 转化为不可变的 [JsonObject]
     */
    open fun asImmutable() = delegate

    /**
     * @see [JsonObject.equals]
     */
    override fun equals(other: Any?) = delegate == other

    /**
     * @see [JsonObject.hashCode]
     */
    override fun hashCode() = delegate.hashCode()

    /**
     * @see [JsonObject.toString]
     */
    override fun toString() = delegate.toString()

    /**
     * [MutableJsonObject] 的序列化器
     * 使用 [MutableJsonObjectSerializer.JsonObjectSerializer] 进行序列化/反序列化
     */
    object MutableJsonObjectSerializer : KSerializer<MutableJsonObject> {

        internal val JsonObjectSerializer: KSerializer<JsonObject> by lazy {
            val clazz = Class.forName("kotlinx.serialization.json.JsonObjectSerializer")
            @Suppress("UNCHECKED_CAST")
            ReflexClass.of(clazz).getInstance() as KSerializer<JsonObject> ?: throw IllegalStateException("Could not get the instance of ${clazz.name}")
        }

        override val descriptor = JsonObjectSerializer.descriptor

        override fun deserialize(decoder: Decoder) = JsonObjectSerializer.deserialize(decoder).asMutable()

        override fun serialize(encoder: Encoder, value: MutableJsonObject) = JsonObjectSerializer.serialize(encoder, value.asImmutable())

    }

}