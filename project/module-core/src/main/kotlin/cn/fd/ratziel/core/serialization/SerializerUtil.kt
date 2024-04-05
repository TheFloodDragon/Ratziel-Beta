package cn.fd.ratziel.core.serialization

import cn.fd.ratziel.core.serialization.serializers.EnhancedListSerializer
import cn.fd.ratziel.core.serialization.serializers.OptionalSerializer
import cn.fd.ratziel.core.serialization.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import java.util.*

val baseSerializers by lazy {
    SerializersModule {
        // UUID
        contextual(UUID::class, UUIDSerializer)
    }
}

typealias Opt<T> = @Serializable(OptionalSerializer::class) Optional<T>
typealias EnhancedList<T> = @Serializable(EnhancedListSerializer::class) List<T>