package cn.fd.ratziel.module.item.impl.component.serializers

import cn.altawk.nbt.NbtDecoder
import cn.altawk.nbt.NbtEncoder
import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.core.serialization.getBy
import cn.fd.ratziel.module.item.impl.component.MetaComponent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject

/**
 * MetaComponentSerializer
 *
 * @author TheFloodDragon
 * @since 2025/4/30 19:33
 */
abstract class MetaComponentSerializer<T : MetaComponent<*>>(
    private val serialName: String, private vararg val alias: String,
) : KSerializer<T> {

    override val descriptor = PrimitiveSerialDescriptor("item.MetaComponent.$serialName", PrimitiveKind.STRING)

    /**
     * 解码 [JsonElement] 生成 [MetaComponent]
     */
    abstract fun decode(element: JsonElement?): T

    /**
     * 将 [MetaComponent] 编码成 [JsonElement]
     */
    abstract fun encode(value: T): JsonElement

    /**
     * 解码 [NbtCompound] 生成 [MetaComponent]
     */
    abstract fun decode(tag: NbtCompound): T

    override fun serialize(encoder: Encoder, value: T) {
        when (encoder) {
            is NbtEncoder -> encoder.encodeNbtTag(value.tag)
            is JsonEncoder -> encoder.encodeJsonElement(this.encode(value))
            else -> throw UnsupportedOperationException("Unsupported Encoder $encoder!")
        }
    }

    override fun deserialize(decoder: Decoder): T {
        return when (decoder) {
            is NbtDecoder -> {
                val tag = decoder.decodeNbtTag()
                if (tag !is NbtCompound) throw IllegalStateException("Invalid NbtTag: '$tag'")
                this.decode(tag)
            }

            is JsonDecoder -> {
                val element = decoder.decodeJsonElement()
                if (element !is JsonObject) throw IllegalStateException("Invalid JsonElement: '$element'")
                val json = element[serialName] ?: element.getBy(*alias)
                this.decode(json)
            }

            else -> throw UnsupportedOperationException("Unsupported Decoder $decoder!")
        }
    }

}