package cn.fd.ratziel.module.nbt

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtList
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.ItemElement
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * NbtSerializer
 *
 * @author TheFloodDragon
 * @since 2025/6/6 22:17
 */
object NbtSerializer : KSerializer<NbtTag> {

    override val descriptor get() = NbtTag.serializer().descriptor

    override fun deserialize(decoder: Decoder): NbtTag {
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            deserializeFromJson(element)
        } else NbtTag.serializer().deserialize(decoder)
    }

    override fun serialize(encoder: Encoder, value: NbtTag) {
        if (encoder is JsonEncoder) {
            encoder.encodeJsonElement(serializeToJson(value))
        } else NbtTag.serializer().serialize(encoder, value)
    }

    /**
     * 将 [JsonElement] 反序列化成 [NbtTag]
     */
    fun deserializeFromJson(json: JsonElement, source: NbtCompound = NbtCompound()): NbtTag =
        when (json) {
            is JsonPrimitive -> deserializeFromString(json.content)
            is JsonArray -> NbtList.of(json.map { deserializeFromJson(it, NbtCompound()) })
            is JsonObject -> source.apply {
                json.forEach {
                    val path = NbtPath(it.key)
                    val newSource = read(path) as? NbtCompound ?: NbtCompound()
                    write(path, deserializeFromJson(it.value, newSource))
                }
            }
        }

    /**
     * 将 [NbtTag] 序列化成 [JsonElement]
     */
    fun serializeToJson(target: NbtTag): JsonElement = when (target) {
        // 特殊类型序列化
        is NbtCompound -> buildJsonObject { target.forEach { put(it.key, serializeToJson(it.value)) } }
        is NbtList -> buildJsonArray { target.forEach { add(serializeToJson(it)) } }
        // 基础类型序列化
        else -> JsonPrimitive(serializeToString(target))
    }

    /**
     * 将字符串反序列化成 [NbtTag]
     */
    fun deserializeFromString(str: String): NbtTag = ItemElement.nbt.decodeFromString(str)

    /**
     * 将 [NbtTag] 序列化成字符串
     */
    fun serializeToString(tag: NbtTag): String = ItemElement.nbt.encodeToString(tag)

}