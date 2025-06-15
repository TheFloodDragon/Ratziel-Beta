package cn.fd.ratziel.module.item.impl.builder

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.core.function.replenish
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import cn.fd.ratziel.module.item.impl.SimpleData
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
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
    val origin: Element,
) : ItemGenerator {

    /**
     * 解释器编排器
     */
    override val compositor = DefaultCompositor()

    override fun build() = build(SimpleContext())

    /**
     * 构建物品
     */
    override fun build(context: ArgumentContext) = buildAsync(context)

    /**
     * 基础物品流 (经过预处理生成的流)
     */
    val baseStream: NativeItemStream = runBlocking {
        // 预解释物品流
        val stream = createNativeStream(SimpleData(), SimpleContext())
        // 预解释任务
        compositor.interpreters.mapNotNull {
            if (it is ItemInterpreter.PreInterpretable) {
                it.preFlow(stream)
            } else null
        }
        return@runBlocking stream
    }

    /**
     * 静态物品策略
     */
    val staticStrategy: StaticStrategy = runBlocking {
        StaticStrategy(baseStream.fetchElement())
    }

    /**
     * 物品流生成
     *
     * @param replenish 每获取一次补充一次 (执行一次 [generateStream])
     */
    val streamGenerating by replenish { generateStream() }

    /**
     * 异步生成物品
     */
    fun buildAsync(context: ArgumentContext) = ItemElement.scope.async {
        // 获取物品流
        val stream = streamGenerating.await()
        // 更新上下文
        stream.context = context

        // 呼出开始生成的事件
        ItemGenerateEvent.Pre(stream.identifier, this@DefaultGenerator, context, origin.property).call()

        if (!staticStrategy.enabled || !staticStrategy.fullStaticMode) {
            // 处理物品流 (非纯静态物品)
            processStream(stream, this)
        }

        // 呼出生成结束的事件
        val event = ItemGenerateEvent.Post(stream.identifier, this@DefaultGenerator, context, stream.item)
        event.call()
        event.item // 返回最终结果
    }.asCompletableFuture()

    /**
     * 生成处理后的物品流
     */
    fun generateStream(): Deferred<NativeItemStream> = ItemElement.scope.async {
        // 复制一下 (必须要复制哈)
        val stream = baseStream.copyWith(SimpleContext())
        // 静态物品处理
        if (staticStrategy.enabled) {
            // 原始元素
            val origin = stream.fetchElement()
            // 生成静态物品
            stream.updateElement(staticStrategy.staticProperty ?: return@async stream)
            // 好戏开场: 处理静态物品流
            processStream(stream, this).join()
            // 换回去
            stream.updateElement(origin)
        }
        return@async stream
    }

    /**
     * 处理物品流 (解释 -> 序列化)
     */
    fun processStream(stream: ItemStream, scope: CoroutineScope) = scope.launch {
        // 解释器解释元素
        val interpreterTasks = compositor.interpreters.map {
            measureTimeMillis {
                it.interpret(stream)
            }.let { t -> debug("[TIME MARK] $it costs $t ms.") }
        }

        // 序列化任务: 元素(解析过后的) -> 组件 -> 数据
        val serializationTasks = ItemRegistry.registry.map { integrated ->
            val element = stream.fetchElement()
            launch {
                val generated = serializeComponent(integrated, element).getOrNull()
                // 合并数据
                if (generated as? NbtCompound != null) stream.data.withValue {
                    // 合并标签
                    it.tag.merge(generated, true)
                }
            }
        }

        // 等待所有序列化任务完成
        serializationTasks.joinAll()
    }

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