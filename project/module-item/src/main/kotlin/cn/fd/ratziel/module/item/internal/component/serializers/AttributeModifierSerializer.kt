@file:OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class)

package cn.fd.ratziel.module.item.internal.component.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.JsonNames
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

/**
 * AttributeModifierSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 12:36
 */
object AttributeModifierSerializer : KSerializer<AttributeModifier> {

    override val descriptor = buildClassSerialDescriptor("bukkit.AttributeModifier") {
        element("uuid", Uuid.serializer().descriptor, listOf(JsonNames("uid")), isOptional = true)
        element<String>("name")
        element<Double>("amount", listOf(JsonNames("amt")))
        element("operation", AttributeOperationSerializer.descriptor, listOf(JsonNames("op")))
        element("slot", EquipmentSlotSerializer.descriptor, isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: AttributeModifier) =
        encoder.encodeStructure(descriptor) {
            encodeNullableSerializableElement(descriptor, 0, Uuid.serializer(), value.uniqueId.toKotlinUuid())
            encodeStringElement(descriptor, 1, value.name)
            encodeDoubleElement(descriptor, 2, value.amount)
            encodeSerializableElement(descriptor, 3, AttributeOperationSerializer, value.operation)
            @Suppress("DEPRECATION")
            encodeNullableSerializableElement(descriptor, 4, EquipmentSlotSerializer, value.slot)
        }

    override fun deserialize(decoder: Decoder) =
        decoder.decodeStructure(descriptor) {
            var uuid: UUID? = null
            var name: String? = null
            var amount = 0.0
            var operation: AttributeModifier.Operation? = null
            var slot: EquipmentSlot? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> uuid = decodeNullableSerializableElement(descriptor, index, Uuid.serializer())?.toJavaUuid()
                    1 -> name = decodeStringElement(descriptor, index)
                    2 -> amount = decodeDoubleElement(descriptor, index)
                    3 -> operation = decodeSerializableElement(descriptor, index, AttributeOperationSerializer)
                    4 -> slot = decodeNullableSerializableElement(descriptor, index, EquipmentSlotSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
            AttributeModifier(uuid ?: UUID.randomUUID(), name!!, amount, operation!!, slot)
        }

}