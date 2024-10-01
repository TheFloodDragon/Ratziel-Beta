package cn.fd.ratziel.module.item

import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.api.builder.ItemTransformer
import java.util.concurrent.ConcurrentHashMap

/**
 * ItemRegistry - 物品注册表
 *
 * @author TheFloodDragon
 * @since 2024/10/1 15:03
 */
object ItemRegistry {

    /**
     * 组件对象类型 - 组件集成构建器
     */
    internal val registry: MutableMap<Class<*>, Integrated<*>> = ConcurrentHashMap()

    /**
     * 注册组件
     *
     * @param serializer 组件序列化器
     * @param transformer 组件转换器
     */
    fun <T> register(type: Class<T>, serializer: ItemSerializer<T>, transformer: ItemTransformer<T>) {
        val integrated = Integrated(serializer, transformer)
        registry[type] = integrated
    }

    /**
     * 获取组件集成构建器
     */
    fun <T> get(type: Class<T>): Integrated<T> {
        @Suppress("UNCHECKED_CAST")
        return registry[type] as Integrated<T>
    }

    /**
     * 获取组件序列化器
     */
    fun <T> getSerializer(type: Class<T>): ItemSerializer<T> {
        return get(type).serializer
    }

    /**
     * 获取组件转换器
     */
    fun <T> getTransformer(type: Class<T>): ItemSerializer<T> {
        return get(type).serializer
    }

    /**
     * 获取优先级排序后的集成构建器列表
     */
    fun getSortedList(): List<Integrated<*>> {
        return registry.values.sortedBy { it.priority }
    }

    /**
     * 集成 [ItemSerializer] 和 [ItemTransformer]
     *
     * @param serializer 序列化器
     * @param transformer 转换器
     * @param priority 优先级
     */
    class Integrated<T>(
        val serializer: ItemSerializer<T>,
        val transformer: ItemTransformer<T>,
        val priority: Int = 0
    ) : ItemSerializer<T> by serializer, ItemTransformer<T> by transformer

}