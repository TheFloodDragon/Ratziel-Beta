package cn.fd.ratziel.common.message.builder

import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.core.serialization.json.isJsonObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
        val content = decoder.decodeString()
        return if (content.isJsonObject()) {
            Message.transformFromJson(content)
        } else {
            val component = Message.buildMessage(content)
            if (Settings.nonItalic && !component.hasDecoration(TextDecoration.ITALIC)) {
                component.decoration(TextDecoration.ITALIC, false)
            } else component
        }
    }

    override fun serialize(encoder: Encoder, value: Component) = encoder.encodeString(Message.transformToJson(value))

}