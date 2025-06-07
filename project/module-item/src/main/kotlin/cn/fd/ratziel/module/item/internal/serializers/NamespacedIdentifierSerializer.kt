package cn.fd.ratziel.module.item.internal.serializers

import cn.fd.ratziel.module.item.impl.NamespacedIdentifier
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * NamespacedIdentifierSerializer
 *
 * @author TheFloodDragon
 * @since 2025/6/7 08:39
 */
object NamespacedIdentifierSerializer : KSerializer<NamespacedIdentifier> {

    override val descriptor = PrimitiveSerialDescriptor("ratziel.NamespacedIdentifier", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: NamespacedIdentifier) = encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): NamespacedIdentifier = NamespacedIdentifier.fromString(decoder.decodeString())

}