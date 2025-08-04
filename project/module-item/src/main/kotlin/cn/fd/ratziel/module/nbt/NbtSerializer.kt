package cn.fd.ratziel.module.nbt

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.*
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
            encoder.encodeJsonElement(serializeToJson(value, true))
        } else NbtTag.serializer().serialize(encoder, value)
    }

    /**
     * 将 [JsonElement] 反序列化成 [NbtTag]
     */
    @JvmStatic
    fun deserializeFromJson(json: JsonElement, source: NbtCompound = NbtCompound()): NbtTag =
        when (json) {
            is JsonPrimitive -> if (json.isString) NbtString(json.content) else deserializeFromString(json.content)
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
    @JvmStatic
    @JvmOverloads
    fun serializeToJson(target: NbtTag, strict: Boolean = false): JsonElement = when (target) {
        // 特殊类型序列化
        is NbtCompound -> buildJsonObject { target.forEach { put(it.key, serializeToJson(it.value, strict)) } }
        is NbtList -> buildJsonArray { target.forEach { add(serializeToJson(it, strict)) } }
        is NbtIntArray -> buildJsonArray { target.content.forEach { add(JsonPrimitive(it)) } }
        is NbtLongArray -> buildJsonArray { target.content.forEach { add(JsonPrimitive(it)) } }
        is NbtByteArray -> buildJsonArray { target.content.forEach { add(JsonPrimitive(it)) } }
        // 基础类型序列化
        is NbtString -> JsonPrimitive(target.content)
        else -> if (strict) {
            JsonPrimitive(target.toString())
        } else {
            JsonPrimitive(target.content as Number)
        }
    }

    /**
     * 将字符串反序列化成 [NbtTag]
     */
    @JvmStatic
    fun deserializeFromString(str: String): NbtTag = ItemElement.nbt.decodeFromString(str)

    /**
     * 将 [NbtTag] 序列化成字符串
     */
    @JvmStatic
    fun serializeToString(tag: NbtTag): String = ItemElement.nbt.encodeToString(tag)

}