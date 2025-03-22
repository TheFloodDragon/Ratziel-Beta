package cn.fd.ratziel.module.item.builder

import cn.fd.ratziel.core.serialization.ContextualSerializer
import cn.fd.ratziel.function.ArgumentContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * SectionTransforming
 *
 * @author TheFloodDragon
 * @since 2024/10/1 14:56
 */
open class SectionTransforming<T : Any>(val serializer: KSerializer<T>) : ContextualSerializer<T>, JsonTransformingSerializer<T>(serializer) {

    override fun accept(context: ArgumentContext) = object : SectionTransforming<T>(serializer) {
        override fun transformDeserialize(element: JsonElement): JsonElement {
            return DefaultSectionResolver.resolve(element, context)
        }
    }

    override fun toString(): String {
        return "SectionTransforming(serializer=$serializer)"
    }

}