package cn.fd.ratziel.common.message.builder

import cn.fd.ratziel.common.message.buildMessage
import cn.fd.ratziel.common.message.toJsonString
import cn.fd.ratziel.core.serialization.primitiveDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component

/**
 * ComponentSerializer - 组件序列化器
 *
 * @author TheFloodDragon
 * @since 2023/10/5 11:42
 */
object ComponentSerializer : KSerializer<Component> {

    override val descriptor = primitiveDescriptor(PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Component = buildMessage(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Component) = encoder.encodeString(value.toJsonString())

}