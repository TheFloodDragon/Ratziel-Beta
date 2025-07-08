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
    private val getter: () -> T,
) : ReadOnlyProperty<Any?, T> {

    private var value: T = getter()

    @Synchronized
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        // 获取当前的值
        val nowValue = value
        // 重新初始化
        value = getter()
        // 返回保存的值
        return nowValue
    }

}

/**
 * 创建一个补充器
 */
fun <T> replenish(getter: () -> T) = Replenishment(getter)
