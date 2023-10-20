@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta

import cn.fd.ratziel.core.serialization.decodeSerializableElement
import cn.fd.ratziel.core.serialization.encodeSerializableElement
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.*

/**
 * ItemMetaSerializer
 * 用于序列化 VItemMeta
 *
 * @author TheFloodDragon
 * @since 2023/10/20 18:59
 */
@Serializer(VItemMeta::class)
object ItemMetaSerializer : KSerializer<VItemMeta> {

    override fun serialize(encoder: Encoder, value: VItemMeta) = encoder.encodeStructure(descriptor) {
        encodeSerializableElement(VItemDisplay.serializer(), 0, value.display)
        encodeSerializableElement(VItemCharacteristic.serializer(), 1, value.characteristic)
        encodeSerializableElement(VItemDurability.serializer(), 2, value.durability)
    }

    override fun deserialize(decoder: Decoder) =
        decoder.decodeStructure(descriptor) {
            var display: VItemDisplay? = null
            var characteristic: VItemCharacteristic? = null
            var durability: VItemDurability? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> display = decodeSerializableElement(VItemDisplay.serializer(), index)
                    1 -> characteristic = decodeSerializableElement(VItemCharacteristic.serializer(), index)
                    2 -> durability = decodeSerializableElement(VItemDurability.serializer(), index)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
            VItemMeta(display!!, characteristic!!, durability!!)
        }

}