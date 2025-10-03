package cn.fd.ratziel.core.functional

import java.lang.ref.WeakReference
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

    private var value: WeakReference<T> = WeakReference(getter())

    override fun getValue(thisRef: Any?, property: KProperty<*>) = this.getValue()

    /**
     * 获取并更新值
     */
    @Synchronized
    fun getValue(): T {
        // 获取当前的值
        val nowValue = value.get()
        // 重新初始化
        val newValue = getter()
        this.value = WeakReference(newValue)
        // 返回保存的值
        return nowValue ?: newValue
    }

}

/**
 * 创建一个补充器
 */
fun <T> replenish(getter: () -> T) = Replenishment(getter)
