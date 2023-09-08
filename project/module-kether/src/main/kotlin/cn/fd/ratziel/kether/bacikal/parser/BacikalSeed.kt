package cn.fd.ratziel.kether.bacikal.parser

import java.util.concurrent.CompletableFuture

/**
 * @author Lanscarlos
 * @since 2023-08-21 10:14
 */
interface BacikalSeed<T> {

    val isAccepted: Boolean

    fun accept(reader: BacikalReader)

    fun accept(frame: BacikalFrame): CompletableFuture<T>

}