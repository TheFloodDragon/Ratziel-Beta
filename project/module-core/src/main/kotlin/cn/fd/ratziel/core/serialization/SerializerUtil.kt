package cn.fd.ratziel.core.serialization

import cn.fd.ratziel.core.serialization.serializers.OptionalSerializer
import cn.fd.ratziel.core.serialization.serializers.UUIDSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.util.*

val baseSerializers by lazy {
    SerializersModule {
        // Optional
        polymorphic(Optional::class) {
            subclass(OptionalSerializer(PolymorphicSerializer(Any::class)))
        }
        // UUID
        contextual(UUID::class, UUIDSerializer)
    }
}

typealias Opt<T> = OptionalC<T>
typealias OptionalC<T> = @Contextual Optional<T>