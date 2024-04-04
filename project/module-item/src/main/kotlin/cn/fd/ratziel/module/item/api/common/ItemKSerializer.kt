package cn.fd.ratziel.module.item.api.common

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder

/**
 * ItemKSerializer - 物品序列化器 兼 Kotlin序列化器 (并默认使用[JsonElement]作为输入类型)
 *
 * @author TheFloodDragon
 * @since 2024/4/4 20:00
 */
interface ItemKSerializer<T : ItemComponent<*, *>> : ItemSerializer<JsonElement, T>, KSerializer<T> {

    /**
     * 重写 [KSerializer] 的 [serialize] 方法
     * 注: 只支持Json格式的序列化
     */
    override fun serialize(encoder: Encoder, value: T) {
        if (encoder is JsonEncoder)
            encoder.encodeJsonElement(this.serialize(value))
        else throw UnsupportedTypeException(encoder)
    }

    /**
     * 重写 [KSerializer] 的 [deserialize] 方法
     * 注: 只支持Json格式的反序列化
     */
    override fun deserialize(decoder: Decoder): T {
        if (decoder is JsonDecoder)
            return this.deserialize(decoder.decodeJsonElement())
        else throw UnsupportedTypeException(decoder)
    }

}