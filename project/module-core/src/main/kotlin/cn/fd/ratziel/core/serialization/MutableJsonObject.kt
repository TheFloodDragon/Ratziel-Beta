package cn.fd.ratziel.core.serialization

import cn.fd.ratziel.function.util.uncheck
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import taboolib.common.io.getInstance

/**
 * MutableJsonObject
 *
 * @author TheFloodDragon
 * @since 2024/5/22 22:09
 */

open class MutableJsonObject(
    val content: MutableMap<String, JsonElement>
) : MutableMap<String, JsonElement> by content {

    constructor() : this(mutableMapOf())

    constructor(content: Map<String, JsonElement>) : this(content.toMutableMap())

    open fun asImmutable() = JsonObject(HashMap(content))

    open val immutable = JsonObject(content)

    override fun equals(other: Any?) = immutable == other

    override fun hashCode() = immutable.hashCode()

    override fun toString() = immutable.toString()

    object MutableJsonObjectSerializer : KSerializer<MutableJsonObject> {

        private val jsonObjectSerializer: KSerializer<JsonObject> by lazy {
            val clazz = Class.forName("kotlinx.serialization.json.JsonObjectSerializer")
            val instance = clazz.getInstance(true) ?: throw IllegalStateException("Could not get the instance of ${clazz.name}")
            uncheck(instance.get())
        }

        override val descriptor = jsonObjectSerializer.descriptor

        override fun deserialize(decoder: Decoder) = MutableJsonObject(jsonObjectSerializer.deserialize(decoder))

        override fun serialize(encoder: Encoder, value: MutableJsonObject) = jsonObjectSerializer.serialize(encoder, value.asImmutable())

    }

}