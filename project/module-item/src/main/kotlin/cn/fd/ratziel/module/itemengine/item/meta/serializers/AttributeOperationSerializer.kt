package cn.fd.ratziel.module.itemengine.item.meta.serializers

import cn.fd.ratziel.core.serialization.primitiveDescriptor
import cn.fd.ratziel.module.itemengine.util.MetaMather
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
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

    override val descriptor = primitiveDescriptor(PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: AttributeModifier.Operation) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder) = MetaMather.matchAttributeOperation(decoder.decodeString())
}