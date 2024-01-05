package cn.fd.ratziel.module.itemengine.item.meta.serializers

import cn.fd.ratziel.core.serialization.primitiveDescriptor
import cn.fd.ratziel.module.itemengine.util.MetaMather
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
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

    override val descriptor = primitiveDescriptor(PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Attribute) {
        encoder.encodeString(value.key.key)
    }

    override fun deserialize(decoder: Decoder) = MetaMather.matchAttribute(decoder.decodeString())
}