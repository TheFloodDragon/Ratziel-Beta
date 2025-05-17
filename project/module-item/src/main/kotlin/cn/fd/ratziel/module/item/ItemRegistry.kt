package cn.fd.ratziel.module.item

import cn.fd.ratziel.module.item.api.builder.DataProcessor
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSource
import cn.fd.ratziel.module.item.exception.ComponentNotFoundException
import cn.fd.ratziel.module.item.internal.builder.ResolvationInterceptor
import cn.fd.ratziel.module.item.internal.builder.SourceInterceptor
import kotlinx.serialization.KSerializer
import java.util.concurrent.CopyOnWriteArrayList

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
    val registry: MutableList<Integrated<*>> = CopyOnWriteArrayList()

    /**
     * 物品解释器注册表
     */
    val interceptors: MutableList<ItemInterceptor> = CopyOnWriteArrayList()

    /**
     * 注册组件
     *
     * @param serializer 组件序列化器
     */
    @JvmStatic
    fun <T> registerComponent(
        type: Class<T>,
        serializer: KSerializer<T>,
        processor: DataProcessor = DataProcessor.NoProcess,
    ) {
        val integrated = Integrated(type, serializer, processor)
        registry.add(integrated)
    }

    /**
     * 获取组件集成构建器
     */
    @JvmStatic
    fun <T> getComponent(type: Class<T>): Integrated<T> {
        val integrated = registry.find { it.type == type }
            ?: throw ComponentNotFoundException(type)
        @Suppress("UNCHECKED_CAST")
        return integrated as Integrated<T>
    }

    /**
     * 注册物品解释器
     */
    @JvmStatic
    fun registerInterceptor(interceptor: ItemInterceptor) {
        interceptors.add(interceptor)
    }

    /**
     * 注册物品源
     */
    @JvmStatic
    fun registerSource(source: ItemSource) {
        val interceptor = SourceInterceptor(source)
        registerInterceptor(interceptor)
    }

    /**
     * 注册解析器
     */
    @JvmStatic
    fun registerResolver(resolver: ItemResolver) {
        val interceptor = ResolvationInterceptor(resolver)
        registerInterceptor(interceptor)
    }

    /**
     * 物品组件集成
     * @param serializer 序列化器
     * @param processor 物品数据处理器
     */
    class Integrated<T>(
        /** 物品组件类型 **/
        val type: Class<T>,
        /** 物品组件序列化器 **/
        val serializer: KSerializer<T>,
        /** 数据处理器 **/
        val processor: DataProcessor,
    )

}