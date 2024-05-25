package cn.fd.ratziel.module.item.impl.part.serializers

import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.attribute.AttributeModifier

/**
 * AttributeOperationSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 12:36
 */
object AttributeOperationSerializer : KSerializer<AttributeModifier.Operation> {

    override val descriptor = PrimitiveSerialDescriptor("bukkit.AttributeModifier.Operation", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: AttributeModifier.Operation) = encoder.encodeString(value.name)

    override fun deserialize(decoder: Decoder) = MetaMatcher.matchAttributeOperation(decoder.decodeString())

}