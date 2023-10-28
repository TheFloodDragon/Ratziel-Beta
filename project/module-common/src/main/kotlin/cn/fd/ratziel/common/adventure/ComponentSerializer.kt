package cn.fd.ratziel.common.adventure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component

/**
 * ComponentSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/5 11:42
 */
object ComponentSerializer : KSerializer<Component> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("common.Component", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Component = buildMessageMJ(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Component) = encoder.encodeString(value.toJsonFormat())
}