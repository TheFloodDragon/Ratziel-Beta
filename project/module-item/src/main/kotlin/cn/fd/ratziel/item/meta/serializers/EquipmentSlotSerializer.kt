package cn.fd.ratziel.item.meta.serializers

import cn.fd.ratziel.item.util.MetaMather
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.EquipmentSlot

/**
 * EquipmentSlotSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 12:33
 */
object EquipmentSlotSerializer : KSerializer<EquipmentSlot?> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("item.EquipmentSlot", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EquipmentSlot?) {
        value?.let { encoder.encodeString(it.name) }
    }

    override fun deserialize(decoder: Decoder): EquipmentSlot? = MetaMather.matchEquipment(decoder.decodeString())
}