package cn.fd.ratziel.core.serialization

import cn.fd.ratziel.function.uncheck
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import taboolib.common.io.getInstance

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

    constructor() : this(mutableMapOf())

    /**
     * [MutableJsonObject] 存储的不可变 [JsonObject] 实例
     * 其 [JsonObject.content] 应该与 [MutableJsonObject.content] 是同一个对象 (内存地址相同)
     */
    protected open val immutable: JsonObject = JsonObject(content)

    /**
     * 转化为不可变的 [JsonObject]
     */
    open fun asImmutable() = immutable

    /**
     * @see [JsonObject.equals]
     */
    override fun equals(other: Any?) = immutable == other

    /**
     * @see [JsonObject.hashCode]
     */
    override fun hashCode() = immutable.hashCode()

    /**
     * @see [JsonObject.toString]
     */
    override fun toString() = immutable.toString()

    /**
     * [MutableJsonObject] 的序列化器
     * 使用 [MutableJsonObjectSerializer.JsonObjectSerializer] 进行序列化/反序列化
     */
    object MutableJsonObjectSerializer : KSerializer<MutableJsonObject> {

        internal val JsonObjectSerializer: KSerializer<JsonObject> by lazy {
            val clazz = Class.forName("kotlinx.serialization.json.JsonObjectSerializer")
            val instance = clazz.getInstance(true) ?: throw IllegalStateException("Could not get the instance of ${clazz.name}")
            uncheck(instance.get())
        }

        override val descriptor = JsonObjectSerializer.descriptor

        override fun deserialize(decoder: Decoder) = JsonObjectSerializer.deserialize(decoder).asMutable()

        override fun serialize(encoder: Encoder, value: MutableJsonObject) = JsonObjectSerializer.serialize(encoder, value.asImmutable())

    }

}