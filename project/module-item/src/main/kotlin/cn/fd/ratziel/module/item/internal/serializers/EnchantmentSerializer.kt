package cn.fd.ratziel.module.item.internal.serializers

import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import taboolib.library.xseries.XEnchantment

/**
 * EnchantmentSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 12:32
 */
object EnchantmentSerializer : KSerializer<XEnchantment> {

    override val descriptor = PrimitiveSerialDescriptor("xseries.Enchantment", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: XEnchantment) {
        val enchant = value.get()!!
        val key = try {
            enchant.key.toString()
        } catch (_: NoSuchMethodException) {
            @Suppress("DEPRECATION")
            enchant.name
        }
        encoder.encodeString(key)
    }

    override fun deserialize(decoder: Decoder) = MetaMatcher.matchEnchantment(decoder.decodeString())

}