package cn.fd.ratziel.module.item.internal.component.serializers

import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.attribute.Attribute

/**
 * AttributeSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 12:34
 */
object AttributeSerializer : KSerializer<Attribute> {

    override val descriptor = PrimitiveSerialDescriptor("bukkit.Attribute", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Attribute) = encoder.encodeString(value.key.key)

    override fun deserialize(decoder: Decoder) = MetaMatcher.matchAttribute(decoder.decodeString()).get()!!

}