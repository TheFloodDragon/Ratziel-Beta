@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta.serializers.vmeta

import cn.fd.ratziel.item.meta.VItemCharacteristic
import cn.fd.ratziel.item.meta.VItemDisplay
import cn.fd.ratziel.item.meta.VItemMeta
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.JsonNames

/**
 * ItemMetaSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:59
 */
object ItemMetaSerializer : KSerializer<VItemMeta> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(VItemMeta.serializer().descriptor.serialName) {
            element<VItemDisplay>("display", isOptional = true)
            element<VItemCharacteristic>("characteristic", listOf(JsonNames("char")), isOptional = true)
        }

    override fun serialize(encoder: Encoder, value: VItemMeta) =
        encoder.encodeStructure(descriptor) {
            encodeNullableSerializableElement(
                VItemDisplay.serializer().descriptor,
                0,
                VItemDisplay.serializer(),
                value.display
            )
            encodeNullableSerializableElement(
                VItemCharacteristic.serializer().descriptor,
                1,
                VItemCharacteristic.serializer(),
                value.characteristic
            )
        }

    override fun deserialize(decoder: Decoder) =
        decoder.decodeStructure(descriptor) {
            var vdisplay: VItemDisplay? = null
            var vcharacteristic: VItemCharacteristic? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> vdisplay = decodeNullableSerializableElement(descriptor, index, VItemDisplay.serializer())
                    1 -> vcharacteristic =
                        decodeNullableSerializableElement(descriptor, index, VItemCharacteristic.serializer())

                    CompositeDecoder.DECODE_DONE -> break
                }
            }
            VItemMeta(
                display = vdisplay,
                characteristic = vcharacteristic
            )
        }

}