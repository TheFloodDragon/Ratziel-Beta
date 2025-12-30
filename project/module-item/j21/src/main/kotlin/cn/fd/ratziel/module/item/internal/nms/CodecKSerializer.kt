package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.NbtDecoder
import cn.altawk.nbt.NbtEncoder
import cn.fd.ratziel.platform.bukkit.nms.share.NMSShare
import cn.fd.ratziel.platform.bukkit.util.toGson
import cn.fd.ratziel.platform.bukkit.util.toKotlinx
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder

/**
 * CodecKSerializer
 * 
 * @author TheFloodDragon
 * @since 2025/11/29 20:47
 */
@NMSShare(12005)
class CodecKSerializer<T>(val codec: Codec<T>) : KSerializer<T> {

    override val descriptor = PrimitiveSerialDescriptor("Codec.$codec", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        if (encoder is JsonEncoder) {
            val json = codec.encodeStart(JsonOps.INSTANCE, value)
                .getOrThrow { error("Failed to save: $it") }
            encoder.encodeJsonElement(json.toKotlinx())
        } else if (encoder is NbtEncoder) {
            val tag = CodecSerialization.saveToTag(codec, value)
            encoder.encodeNbtTag(tag)
        }
        error("Unsupported encoder: $encoder")
    }

    override fun deserialize(decoder: Decoder): T {
        if (decoder is JsonDecoder) {
            val json = decoder.decodeJsonElement().toGson()
            return codec.parse(JsonOps.INSTANCE, json)
                .getOrThrow { error("Failed to parse: $it") }
        } else if (decoder is NbtDecoder) {
            val tag = decoder.decodeNbtTag()
            return CodecSerialization.parseFromTag(codec, tag)
        }
        error("Unsupported decoder: $decoder")
    }

}