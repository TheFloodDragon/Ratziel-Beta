package cn.fd.ratziel.core.serialization

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

typealias Opt<T> = OptionalC<T>
typealias OptionalC<T> = @Serializable(OptionalSerializer::class) Optional<T>