package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.ContextualSerializer
import cn.fd.ratziel.core.serialization.elementAlias
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement

/**
 * SectionTransforming
 *
 * @author TheFloodDragon
 * @since 2024/10/1 14:56
 */
open class SectionTransforming<T>(val serializer: KSerializer<T>) : ContextualSerializer<T>, KSerializer<T> by serializer {

    override fun accept(context: ArgumentContext): KSerializer<T> =
        object : SectionTransforming<T>(this.serializer) {
            override fun transformDeserialize(element: JsonElement): JsonElement {
                return SectionResolver.resolve(element, serializer.descriptor.elementAlias, context)
            }
        }

    open fun transformDeserialize(element: JsonElement): JsonElement = element

    final override fun deserialize(decoder: Decoder): T {
        return if (decoder is JsonDecoder) {
            val transformed = transformDeserialize(decoder.decodeJsonElement())
            decoder.json.decodeFromJsonElement(serializer, transformed)
        } else serializer.deserialize(decoder)
    }

    override fun toString(): String {
        return "SectionTransforming(serializer=$serializer)"
    }

}