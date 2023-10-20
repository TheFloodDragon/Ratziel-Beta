package cn.fd.ratziel.core.serialization

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder

fun <T : Any?> CompositeEncoder.encodeSerializableElement(
    serializer: SerializationStrategy<T>,
    index: Int,
    value: T,
) = encodeSerializableElement(serializer.descriptor, index, serializer, value)

fun <T : Any?> CompositeDecoder.decodeSerializableElement(
    serializer: DeserializationStrategy<T>,
    index: Int,
    previousValue: T? = null
) = decodeSerializableElement(serializer.descriptor, index, serializer, previousValue)