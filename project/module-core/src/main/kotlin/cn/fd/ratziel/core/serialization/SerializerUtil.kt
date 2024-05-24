package cn.fd.ratziel.core.serialization

import cn.fd.ratziel.core.serialization.serializers.EnhancedListSerializer
import cn.fd.ratziel.core.serialization.serializers.OptionalSerializer
import kotlinx.serialization.Serializable
import java.util.*

typealias Opt<T> = @Serializable(OptionalSerializer::class) Optional<T>
typealias EnhancedList<T> = @Serializable(EnhancedListSerializer::class) List<T>