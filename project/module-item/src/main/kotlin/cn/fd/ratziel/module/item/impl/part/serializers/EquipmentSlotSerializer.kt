package cn.fd.ratziel.module.item.impl.part.serializers

import cn.fd.ratziel.core.serialization.primitiveDescriptor
import cn.fd.ratziel.module.item.util.MetaMather
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
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

    override val descriptor = primitiveDescriptor(PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EquipmentSlot?) {
        value?.let { encoder.encodeString(it.name) }
    }

    override fun deserialize(decoder: Decoder): EquipmentSlot? = MetaMather.matchEquipment(decoder.decodeString())
}