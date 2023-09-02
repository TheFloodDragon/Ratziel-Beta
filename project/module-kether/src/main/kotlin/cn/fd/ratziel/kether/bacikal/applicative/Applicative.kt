package cn.fd.ratziel.kether.bacikal.applicative

import kotlin.reflect.KProperty

/**
 * @author Lanscarlos
 * @since 2023-08-21 13:55
 */
interface Applicative<T> {

    fun getValue(): T?

    fun getValue(def: T): T

    /**
     * 兼容代理属性
     * */
    operator fun getValue(parent: Any?, property: KProperty<*>): T?

}