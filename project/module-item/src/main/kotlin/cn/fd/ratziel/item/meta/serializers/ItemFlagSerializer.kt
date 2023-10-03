package cn.fd.ratziel.item.meta.serializers

import cn.fd.ratziel.item.meta.matchItemFlag
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.ItemFlag

/**
 * ItemFlagSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 19:54
 */
object ItemFlagSerializer : KSerializer<ItemFlag> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("item.ItemFlag", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ItemFlag = matchItemFlag(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: ItemFlag) = encoder.encodeString(value.name)
}