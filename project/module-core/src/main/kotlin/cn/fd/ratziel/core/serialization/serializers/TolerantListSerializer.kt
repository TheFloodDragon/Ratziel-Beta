package cn.fd.ratziel.core.serialization.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * TolerantListSerializer
 *
 * @author TheFloodDragon
 * @since 2025/3/23 09:40
 */
class TolerantListSerializer<T>(serializer: KSerializer<T>) : JsonTransformingSerializer<List<T>>(ListSerializer(serializer)) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element as? JsonArray ?: JsonArray(listOf(element))
    }

    override fun transformSerialize(element: JsonElement): JsonElement {
        return element as? JsonArray ?: JsonArray(listOf(element))
    }

}