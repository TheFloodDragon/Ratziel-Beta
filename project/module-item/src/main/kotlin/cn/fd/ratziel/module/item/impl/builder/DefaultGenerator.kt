package cn.fd.ratziel.module.item.impl.builder

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import cn.fd.ratziel.module.item.impl.SimpleData
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import taboolib.common.platform.function.debug
import taboolib.common.platform.function.severe
import kotlin.system.measureTimeMillis

/**
 * DefaultGenerator
 *
 * @author TheFloodDragon
 * @since 2025/3/22 15:32
 */
class DefaultGenerator(
    /**
     * 原始物品配置 (元素)
     */
    val origin: Element
) : ItemGenerator {

    override fun build() = build(SimpleContext())

    /**
     * 构建物品
     */
    override fun build(context: ArgumentContext) = buildAsync(SimpleData(), context)

    /**
     * 异步生成物品
     *
     * 注: 此处使用多线程进行 反序列化和应用数据 的操作
     */
    fun buildAsync(sourceData: ItemData, context: ArgumentContext) = ItemElement.scope.async {
        // 创建物品流
        val stream = createNativeStream(sourceData, context)

        // 呼出开始生成的事件
        ItemGenerateEvent.Pre(stream.identifier, this@DefaultGenerator, context, origin.property).call()

        // 解释器解释元素
        val interceptorTasks = ItemRegistry.interceptors.map { interceptor ->
            launch {
                measureTimeMillis {
                    interceptor.intercept(stream)
                }.let { debug("[TIME MARK] ItemInterceptor#$interceptor costs ${it}ms.") }
            }
        }

        // 序列化任务需要完全在解释后, 故等待解释任务的完成
        interceptorTasks.joinAll()

        // 序列化任务: 元素(解析过后的) -> 组件 -> 数据
        val serializationTasks = ItemRegistry.registry.map { integrated ->
            launch {
                val generated = serializeComponent(integrated, stream.fetchElement()).getOrNull()
                // 合并数据
                if (generated as? NbtCompound != null) stream.data.withValue {
                    // 合并标签
                    it.tag.merge(generated, true)
                }
            }
        }

        // 等待所有序列化任务完成
        serializationTasks.joinAll()

        // 呼出生成结束的事件
        val event = ItemGenerateEvent.Post(stream.identifier, this@DefaultGenerator, context, stream.item)
        event.call()
        event.item // 返回最终结果
    }.asCompletableFuture()

    /**
     * 创建原生物品流
     */
    fun createNativeStream(sourceData: ItemData, context: ArgumentContext): NativeItemStream {
        // 生成基本物品 (本地源物品)
        val item = NativeSource.generateItem(origin, sourceData)
            ?: throw IllegalStateException("Failed to generate item source!")
        // 创建物品流
        val stream = NativeItemStream(origin, item, context)
        // 将物品流放入上下文中 (某些情况需要用)
        context.put(stream)
        return stream
    }

    /**
     * 序列化组件
     */
    fun serializeComponent(
        integrated: ItemRegistry.Integrated<*>,
        element: JsonElement,
    ): Result<NbtTag> {
        // 获取序列化器
        @Suppress("UNCHECKED_CAST")
        val serializer = (integrated as ItemRegistry.Integrated<Any>).serializer
        // 第一步: 解码成物品组件
        val component = try {
            // 解码
            ItemElement.json.decodeFromJsonElement(serializer, element)
        } catch (ex: Exception) {
            severe("Failed to deserialize element by '$serializer'!")
            ex.printStackTrace()
            return Result.failure(ex)
        }
        // 第二步: 编码成组件数据
        try {
            // 编码
            val tag = ItemElement.nbt.encodeToNbtTag(serializer, component)
            return Result.success(tag)
        } catch (ex: Exception) {
            severe("Failed to transform component by '$serializer'! Source component: $component")
            ex.printStackTrace()
            return Result.failure(ex)
        }
    }

}