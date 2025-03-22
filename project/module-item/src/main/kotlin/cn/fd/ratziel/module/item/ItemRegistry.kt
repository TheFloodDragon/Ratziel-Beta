package cn.fd.ratziel.module.item

import cn.fd.ratziel.module.item.api.builder.DataProcessor
import kotlinx.serialization.KSerializer
import java.util.concurrent.CopyOnWriteArraySet

/**
 * ItemRegistry - 物品注册表
 *
 * @author TheFloodDragon
 * @since 2024/10/1 15:03
 */
object ItemRegistry {

    /**
     * 组件集成注册表
     */
    val registry: MutableCollection<Integrated<*>> = CopyOnWriteArraySet()

    /**
     * 注册组件
     *
     * @param serializer 组件序列化器
     */
    fun <T> register(
        type: Class<T>,
        serializer: KSerializer<T>,
        processor: DataProcessor = DataProcessor.NoProcess,
        priority: Int = 0
    ) {
        val integrated = Integrated(type, serializer, processor, priority)
        registry.add(integrated)
    }

    /**
     * 获取组件集成构建器
     */
    fun <T> get(type: Class<T>): Integrated<T> {
        @Suppress("UNCHECKED_CAST")
        return (registry.find { it.type == type }
            ?: throw NoSuchElementException("Cannot find '$type' in registry.")) as Integrated<T>
    }

    inline fun <reified T> get(): Integrated<T> = get(T::class.java)

    /**
     * @param serializer 序列化器
     * @param priority 优先级
     */
    class Integrated<T>(
        val type: Class<T>,
        val serializer: KSerializer<T>,
        val processor: DataProcessor,
        val priority: Int,
    )

}