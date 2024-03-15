package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.serialization.primitiveDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * NBTSerializer
 *
 * @author TheFloodDragon
 * @since 2024/3/15 21:40
 */
object NBTSerializer : KSerializer<NBTCompound> {

    override val descriptor = primitiveDescriptor(PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): NBTCompound {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: NBTCompound) {
        TODO("Not yet implemented")
    }

}