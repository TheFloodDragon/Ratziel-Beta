package cn.fd.ratziel.module.item.impl.part.serializers

import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * ItemMaterialSerializer
 *
 * @author TheFloodDragon
 * @since 2024/5/19 09:50
 */
object ItemMaterialSerializer : KSerializer<ItemMaterial> {

    override val descriptor = PrimitiveSerialDescriptor("item.ItemMaterial", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ItemMaterial = MetaMatcher.matchMaterial(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: ItemMaterial) = encoder.encodeString(value.name)

}