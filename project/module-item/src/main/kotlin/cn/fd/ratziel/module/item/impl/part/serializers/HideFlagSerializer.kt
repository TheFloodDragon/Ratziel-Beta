package cn.fd.ratziel.module.item.impl.part.serializers

import cn.fd.ratziel.module.item.api.part.HideFlag
import cn.fd.ratziel.module.item.util.MetaMather
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

    override fun deserialize(decoder: Decoder): HideFlag = MetaMather.matchHideFlag(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: HideFlag) = encoder.encodeString(value.name)

}