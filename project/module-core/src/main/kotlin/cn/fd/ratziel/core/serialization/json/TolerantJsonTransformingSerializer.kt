package cn.fd.ratziel.core.serialization.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * TolerantJsonTransformingSerializer
 *
 * @author TheFloodDragon
 * @since 2025/6/7 08:45
 */
abstract class TolerantJsonTransformingSerializer<T : Any>(private val tSerializer: KSerializer<T>) : KSerializer<T> {

    /**
     * @see JsonTransformingSerializer.descriptor
     */
    override val descriptor: SerialDescriptor get() = tSerializer.descriptor

    /**
     * 代理的 [JsonTransformingSerializer]
     */
    private val proxySerializer by lazy {
        ProxyJsonTransformingSerializer(this)
    }

    final override fun serialize(encoder: Encoder, value: T) {
        if (encoder is JsonEncoder) {
            proxySerializer.serialize(encoder, value)
        } else tSerializer.serialize(encoder, value)
    }

    final override fun deserialize(decoder: Decoder): T {
        return if (decoder is JsonDecoder) {
            proxySerializer.deserialize(decoder)
        } else tSerializer.deserialize(decoder)
    }

    /**
     * @see JsonTransformingSerializer.transformDeserialize
     */
    protected open fun transformDeserialize(element: JsonElement): JsonElement = element

    /**
     * @see JsonTransformingSerializer.transformSerialize
     */
    protected open fun transformSerialize(element: JsonElement): JsonElement = element

    private class ProxyJsonTransformingSerializer<T : Any>(
        private val serializer: TolerantJsonTransformingSerializer<T>,
    ) : JsonTransformingSerializer<T>(serializer.tSerializer) {
        override fun transformDeserialize(element: JsonElement): JsonElement = serializer.transformDeserialize(element)
        override fun transformSerialize(element: JsonElement): JsonElement = serializer.transformSerialize(element)
    }

}