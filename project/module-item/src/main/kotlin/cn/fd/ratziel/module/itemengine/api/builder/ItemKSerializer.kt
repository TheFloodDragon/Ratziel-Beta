package cn.fd.ratziel.module.itemengine.api.builder

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder

/**
 * ItemKSerializer - 物品序列化器 兼 Kotlin序列化器
 *
 * @author TheFloodDragon
 * @since 2024/1/26 16:43
 */
interface ItemKSerializer<T> : ItemSerializer<T>, KSerializer<T> {

    /**
     * 重写 [KSerializer] 的 [serialize] 方法
     * 注: 只支持Json格式的序列化
     */
    override fun serialize(encoder: Encoder, value: T) {
        if (encoder is JsonEncoder)
            encoder.encodeJsonElement(serializeToJson(value))
        else throw UnsupportedTypeException(encoder)
    }

    /**
     * 重写 [KSerializer] 的 [deserializeFromJson] 方法
     * 注: 只支持Json格式的反序列化
     */
    override fun deserialize(decoder: Decoder): T {
        if (decoder is JsonDecoder)
            return deserializeFromJson(decoder.decodeJsonElement())
        else throw UnsupportedTypeException(decoder)
    }

}