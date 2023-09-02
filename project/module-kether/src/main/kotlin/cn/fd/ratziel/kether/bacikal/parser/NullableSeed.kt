package cn.fd.ratziel.kether.bacikal.parser

import java.util.concurrent.CompletableFuture

/**
 * @author Lanscarlos
 * @since 2023-08-21 18:03
 */
class NullableSeed<T>(val seed: BacikalSeed<T>) : BacikalSeed<T?> {

    override val isAccepted: Boolean
        get() = seed.isAccepted

    override fun accept(reader: BacikalReader) {
        seed.accept(reader)
    }

    override fun accept(frame: BacikalFrame): CompletableFuture<T?> {
        return seed.accept(frame).thenApply { it }
    }
}