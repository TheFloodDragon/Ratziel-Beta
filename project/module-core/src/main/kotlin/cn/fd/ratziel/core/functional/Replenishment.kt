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
     * 获取并更新值 (每一次获取的值不一样)
     */
    @Synchronized
    fun getValue(): T {
        // 获取当前的值
        val nowValue = value.get()
        // 重新初始化
        val nextValue = getter()
        // 如果当前值被清理了
        if (nowValue == null) {
            this.value = WeakReference(getter()) // 再初始化一个值
            return nextValue // 返回前一个初始化的值
        } else {
            // 设置新值
            this.value = WeakReference(nextValue)
            // 返回当前的值
            return nowValue
        }
    }

}

/**
 * 创建一个补充器
 */
fun <T> replenish(getter: () -> T) = Replenishment(getter)
