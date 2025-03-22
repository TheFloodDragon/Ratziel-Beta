package cn.fd.ratziel.module.item.impl.serialization.serializers

import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.EquipmentSlot

/**
 * EquipmentSlotSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 12:33
 */
object EquipmentSlotSerializer : KSerializer<EquipmentSlot> {

    override val descriptor = PrimitiveSerialDescriptor("bukkit.EquipmentSlot", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EquipmentSlot) = encoder.encodeString(value.name)

    override fun deserialize(decoder: Decoder): EquipmentSlot = MetaMatcher.matchEquipment(decoder.decodeString()).bukkit

}