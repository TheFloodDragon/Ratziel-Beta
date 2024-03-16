@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.core.serialization.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

/**
 * OptionalSerializer
 *
 * @author TheFloodDragon
 * @since 2024/3/16 11:14
 */
class OptionalSerializer<T : Any>(private val serializer: KSerializer<T>) : KSerializer<Optional<T>> {

    override val descriptor get() = serializer.descriptor

    override fun serialize(encoder: Encoder, value: Optional<T>) = encoder.encodeNullableSerializableValue(serializer, value.orElse(null))

    override fun deserialize(decoder: Decoder) = Optional.ofNullable(decoder.decodeNullableSerializableValue(serializer))

}