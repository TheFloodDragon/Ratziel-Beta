package cn.fd.ratziel.module.item.internal.serializers

import cn.altawk.nbt.NbtDecoder
import cn.altawk.nbt.NbtEncoder
import cn.fd.ratziel.common.config.Settings
import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.module.nbt.NbtSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import taboolib.module.nms.MinecraftVersion

/**
 * MessageComponentSerializer - 消息组件序列化器
 *
 * @author TheFloodDragon
 * @since 2023/10/5 11:42
 */
object MessageComponentSerializer : KSerializer<Component> {

    private val isCompoundStructure = MinecraftVersion.versionId >= 12105

    override val descriptor = PrimitiveSerialDescriptor("adventure.Component", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Component {
        return if (decoder is NbtDecoder) {
            // 1.21.5+
            if (isCompoundStructure) {
                val json = NbtSerializer.serializeToJson(decoder.decodeNbtTag())
                Message.transformFromJson(json.toString())
            } else {
                Message.transformFromJson(decoder.decodeString())
            }
        } else {
            val component = Message.buildMessage(decoder.decodeString())
            // 默认非斜体处理
            if (Settings.nonItalic && !component.hasDecoration(TextDecoration.ITALIC)) {
                component.decoration(TextDecoration.ITALIC, false)
            } else component
        }
    }

    override fun serialize(encoder: Encoder, value: Component) {
        if (encoder is NbtEncoder) {
            // 1.21.5+
            if (isCompoundStructure) {
                val json = Json.parseToJsonElement(Message.transformToJson(value))
                encoder.encodeNbtTag(NbtSerializer.deserializeFromJson(json))
            } else {
                encoder.encodeString(Message.transformToJson(value))
            }
        } else {
            encoder.encodeString(Message.wrapper.miniBuilder.serialize(value))
        }
    }

}