package cn.fd.ratziel.core.serialization.serializers

import cn.fd.ratziel.core.serialization.primitiveDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

/**
 * UUIDSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 12:07
 */
object UUIDSerializer : KSerializer<UUID> {

    override val descriptor = primitiveDescriptor(PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())

}