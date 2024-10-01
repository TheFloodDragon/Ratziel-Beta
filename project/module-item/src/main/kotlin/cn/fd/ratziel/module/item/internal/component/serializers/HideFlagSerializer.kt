package cn.fd.ratziel.module.item.internal.component.serializers

import cn.fd.ratziel.module.item.internal.component.HideFlag
import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * HideFlagSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 19:54
 */
object HideFlagSerializer : KSerializer<HideFlag> {

    override val descriptor = PrimitiveSerialDescriptor("bukkit.HideFlag", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): HideFlag = MetaMatcher.matchHideFlag(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: HideFlag) = encoder.encodeString(value.name)

}