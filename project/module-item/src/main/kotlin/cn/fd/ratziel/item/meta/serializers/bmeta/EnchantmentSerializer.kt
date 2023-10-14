package cn.fd.ratziel.item.meta.serializers.bmeta

import cn.fd.ratziel.item.meta.util.MetaMather
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.enchantments.Enchantment

/**
 * EnchantmentSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 12:32
 */
object EnchantmentSerializer : KSerializer<Enchantment> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("item.Enchantment", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Enchantment) =
        encoder.encodeString(value.key.key)

    override fun deserialize(decoder: Decoder) = MetaMather.matchEnchantment(decoder.decodeString())
}