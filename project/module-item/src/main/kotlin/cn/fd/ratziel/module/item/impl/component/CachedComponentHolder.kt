package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.component.ComponentHolder
import cn.fd.ratziel.module.item.api.component.ItemComponentType

/**
 * CachedComponentHolder
 * 
 * @author TheFloodDragon
 * @since 2025/12/31 00:02
 */
abstract class CachedComponentHolder<Raw> : ComponentHolder {

    /**
     * 获取原始数据
     */
    abstract fun getRaw(type: ItemComponentType<*>): Raw?

    /**
     * 设置原始数据
     */
    abstract fun setRaw(type: ItemComponentType<*>, raw: Raw?)

    /**
     * 删除原始数据
     */
    abstract fun removeRaw(type: ItemComponentType<*>)

    /**
     * 原始数据 -> 操作数据
     */
    abstract fun <T : Any> exchangeFromRaw(type: ItemComponentType<T>, raw: Raw): T

    /**
     * 操作数据 -> 原始数据
     */
    abstract fun <T : Any> exchangeToRaw(type: ItemComponentType<T>, value: T): Raw

    /**
     * 交换数据缓存
     */
    private val cache = LinkedHashMap<ItemComponentType<*>, Pair<Raw, Any>>()

    final override fun <T : Any> get(type: ItemComponentType<T>): T? {
        val raw = getRaw(type) ?: return null
        // 尝试获取缓存
        val record = cache[type]
        if (record != null && record.first == raw) {
            // 当前记录的和原始数据保持一致, 直接返回转换后的
            @Suppress("UNCHECKED_CAST")
            return record.second as T
        } else {
            // 不一致或者不存在缓存, 重新交换数据
            val exchanged = exchangeFromRaw<T>(type, raw)
            // 缓存新交换的数据
            cache[type] = raw to exchanged
            return exchanged
        }
    }

    final override fun <T : Any> set(type: ItemComponentType<T>, value: T) {
        // 转换传入数据
        val exchanged = exchangeToRaw(type, value)
        // 设置组件
        setRaw(type, exchanged)
        // 记录缓存
        cache[type] = exchanged to value
    }

    final override fun remove(type: ItemComponentType<*>) {
        removeRaw(type)
        // 清除缓存
        cache.remove(type)
    }

    final override fun restore(type: ItemComponentType<*>) {
        setRaw(type, null)
        // 清除缓存
        cache.remove(type)
    }

}