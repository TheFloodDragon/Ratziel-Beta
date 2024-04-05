package cn.fd.ratziel.common.message.builder

import cn.fd.ratziel.common.message.Message
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component

/**
 * MessageComponentSerializer - 消息组件序列化器
 *
 * @author TheFloodDragon
 * @since 2023/10/5 11:42
 */
object MessageComponentSerializer : KSerializer<Component> {

    override val descriptor = PrimitiveSerialDescriptor("adventure.Component", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Component = Message.buildMessage(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Component) = encoder.encodeString(Message.transformToJson(value))

}