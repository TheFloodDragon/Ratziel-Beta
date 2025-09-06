package cn.fd.ratziel.core.functional

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * SynchronizedValue
 *
 * @author TheFloodDragon
 * @since 2025/9/6 20:38
 */
class SynchronizedValue<T>(private var value: T) : ReadWriteProperty<Any?, T> {

    @Synchronized
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return this.value
    }

    @Synchronized
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

}

fun <T> synchronized(initializer: () -> T) = SynchronizedValue(initializer())
