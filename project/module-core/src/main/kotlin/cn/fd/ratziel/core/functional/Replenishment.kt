package cn.fd.ratziel.core.functional

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Replenishment
 *
 * @author TheFloodDragon
 * @since 2025/5/11 10:07
 */
class Replenishment<T>(
    private val initializer: () -> T,
) : ReadOnlyProperty<Any?, T> {

    private var value: T = initializer()

    @Synchronized
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        // 获取当前的值
        val nowValue = value
        // 重新初始化
        value = initializer()
        // 返回保存的值
        return nowValue
    }

}

/**
 * 创建一个补充器
 */
fun <T> replenish(initializer: () -> T) = Replenishment(initializer)
