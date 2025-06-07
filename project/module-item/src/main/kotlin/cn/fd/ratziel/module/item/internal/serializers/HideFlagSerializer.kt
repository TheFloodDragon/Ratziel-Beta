package cn.fd.ratziel.module.item.internal.serializers

import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import taboolib.library.xseries.XItemFlag

/**
 * HideFlagSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 19:54
 */
object HideFlagSerializer : KSerializer<XItemFlag> {

    override val descriptor = PrimitiveSerialDescriptor("xseries.HideFlag", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): XItemFlag = MetaMatcher.matchHideFlag(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: XItemFlag) = encoder.encodeString(value.name)

}