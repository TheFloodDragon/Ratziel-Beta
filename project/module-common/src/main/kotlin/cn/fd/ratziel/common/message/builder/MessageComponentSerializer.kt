package cn.fd.ratziel.common.message.builder

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.message.Message
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

/**
 * MessageComponentSerializer - 消息组件序列化器
 *
 * @author TheFloodDragon
 * @since 2023/10/5 11:42
 */
object MessageComponentSerializer : KSerializer<Component> {

    override val descriptor = PrimitiveSerialDescriptor("adventure.Component", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Component {
        return if (decoder is JsonDecoder) {
            val component = Message.buildMessage(decoder.decodeString())
            // 默认非斜体处理
            if (Settings.nonItalic && !component.hasDecoration(TextDecoration.ITALIC)) {
                component.decoration(TextDecoration.ITALIC, false)
            } else component
        } else {
            Message.transformFromJson(decoder.decodeString())
        }
    }

    override fun serialize(encoder: Encoder, value: Component) {
        if (encoder is JsonEncoder) {
            encoder.encodeString(Message.wrapper.miniBuilder.serialize(value))
        } else {
            encoder.encodeString(Message.transformToJson(value))
        }
    }

}