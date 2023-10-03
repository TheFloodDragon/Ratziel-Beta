package cn.fd.ratziel.item.meta.serializers

import cn.fd.ratziel.core.serialization.serializers.UUIDJsonSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import java.util.*

/**
 * AttributeModifierSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 12:36
 */
object AttributeModifierSerializer : KSerializer<AttributeModifier> {

    override val descriptor = buildClassSerialDescriptor("AttributeModifier") {
        element<UUID>("uuid")
        element<String>("name")
        element<Double>("amount")
        element<AttributeModifier.Operation>("operation")
        element<EquipmentSlot>("slot")
    }

    override fun serialize(encoder: Encoder, value: AttributeModifier) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, UUIDJsonSerializer, value.uniqueId)
            encodeStringElement(descriptor, 1, value.name)
            encodeDoubleElement(descriptor, 2, value.amount)
            encodeSerializableElement(descriptor, 3, AttributeOperationSerializer, value.operation)
            encodeSerializableElement(descriptor, 4, EquipmentSlotSerializer, value.slot ?: EquipmentSlot.HAND)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder) =
        decoder.decodeStructure(descriptor) {
            var uuid: UUID? = null
            var name: String? = null
            var amount: Double? = null
            var operation: AttributeModifier.Operation? = null
            var slot: EquipmentSlot? = null
            if (decodeSequentially()) { // 顺序解码协议
                uuid = decodeSerializableElement(descriptor, 0, UUIDJsonSerializer)
                name = decodeStringElement(descriptor, 1)
                amount = decodeDoubleElement(descriptor, 2)
                operation = decodeSerializableElement(descriptor, 3, AttributeOperationSerializer)
                slot = decodeSerializableElement(descriptor, 4, EquipmentSlotSerializer)
            } else while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> uuid = decodeSerializableElement(descriptor, index, UUIDJsonSerializer)
                    1 -> name = decodeStringElement(descriptor, index)
                    2 -> amount = decodeDoubleElement(descriptor, index)
                    3 -> operation = decodeSerializableElement(descriptor, index, AttributeOperationSerializer)
                    4 -> slot = decodeSerializableElement(descriptor, index, EquipmentSlotSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
            AttributeModifier(uuid ?: UUID.randomUUID(), name!!, amount!!, operation!!, slot)
        }

}