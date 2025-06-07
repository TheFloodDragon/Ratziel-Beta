package cn.fd.ratziel.module.item.internal.serializers

import cn.fd.ratziel.core.serialization.json.TolerantJsonTransformingSerializer
import cn.fd.ratziel.module.item.impl.component.SoundInstance
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * SoundInstanceTransformer
 *
 * @author TheFloodDragon
 * @since 2025/6/7 08:43
 */
object SoundInstanceSerializer : TolerantJsonTransformingSerializer<SoundInstance>(SoundInstance.generatedSerializer()) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element is JsonPrimitive) {
            return buildJsonObject {
                // SoundInstance#sound
                put(descriptor.getElementDescriptor(0).serialName, element)
            }
        }
        return element
    }

}