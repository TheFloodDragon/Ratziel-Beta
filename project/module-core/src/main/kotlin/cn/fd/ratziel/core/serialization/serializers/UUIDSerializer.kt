@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.core.serialization.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import java.util.*

/**
 * UUIDSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 12:07
 */
object UUIDSerializer : KSerializer<UUID> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())

}

@Serializer(forClass = UUID::class)
object UUIDJsonSerializer : JsonTransformingSerializer<UUID>(UUIDSerializer) {

    override fun transformDeserialize(element: JsonElement): JsonElement =
        element.takeUnless { it is JsonNull } ?: JsonPrimitive(UUID.randomUUID().toString())

}