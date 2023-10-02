@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.serializer
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import java.util.*


object EnchantmentSerializer : KSerializer<Enchantment> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Enchantment", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Enchantment) {
        encoder.encodeString(value.key.key)
    }

    override fun deserialize(decoder: Decoder) = matchEnchantment(decoder.decodeString())
}


object EquipmentSlotSerializer : KSerializer<EquipmentSlot> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EquipmentSlot", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EquipmentSlot) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): EquipmentSlot = matchEquipment(decoder.decodeString())
}


object AttributeSerializer : KSerializer<Attribute> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Attribute", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Attribute) {
        encoder.encodeString(value.key.key)
    }

    override fun deserialize(decoder: Decoder) = matchAttribute(decoder.decodeString())
}


object AttributeOperationSerializer : KSerializer<AttributeModifier.Operation> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AttributeOperation", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: AttributeModifier.Operation) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder) = matchAttributeOperation(decoder.decodeString())
}


object AttributeModifierSerializer : KSerializer<AttributeModifier> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AttributeModifier", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: AttributeModifier) {
        encoder.encodeSerializableValue(
            serializer<SerializableAttributeModifier>(), SerializableAttributeModifier(
                value.uniqueId.toString(), value.name, value.amount, value.operation, value.slot ?: EquipmentSlot.HAND
            )
        )
    }

    override fun deserialize(decoder: Decoder) =
        decoder.decodeSerializableValue(serializer<SerializableAttributeModifier>()).build()

    @Serializable
    class SerializableAttributeModifier(
        @JsonNames("uid") val uuid: String = UUID.randomUUID().toString(),
        val name: String,
        val amount: Double,
        @Serializable(with = AttributeOperationSerializer::class) val operation: AttributeModifier.Operation,
        @Serializable(with = EquipmentSlotSerializer::class) val slot: EquipmentSlot,
    ) {
        fun build(): AttributeModifier = AttributeModifier(UUID.fromString(uuid), name, amount, operation, slot)
    }

}

