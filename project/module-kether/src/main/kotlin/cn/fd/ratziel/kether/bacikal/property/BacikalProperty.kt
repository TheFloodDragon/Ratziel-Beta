package cn.fd.ratziel.kether.bacikal.property

import kotlin.reflect.KClass

/**
 * @author Lanscarlos
 * @since 2023-08-25 00:59
 */
annotation class BacikalProperty(val id: String, val bind: KClass<*>)
