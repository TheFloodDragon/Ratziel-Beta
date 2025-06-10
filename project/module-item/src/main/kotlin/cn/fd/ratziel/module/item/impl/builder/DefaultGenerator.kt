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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import taboolib.common.platform.function.debug
import taboolib.common.platform.function.severe
import taboolib.common.reflect.getAnnotationIfPresent
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

    override fun build() = build(SimpleContext())

    /**
     * 构建物品
     */
    override fun build(context: ArgumentContext) = buildAsync(context)

    /**
     * 基础物品流 (经过预处理生成的流)
     */
    val baseStream = ItemElement.scope.async {
        // 预解释物品流
        val stream = createNativeStream(SimpleData(), SimpleContext())
        // 预解释任务
        val tasks = ItemRegistry.interpreters
            .filter { getPreInterpretable(it)?.onlyPre == true }
            .map { launch { it.interpret(stream) } }
        // 等待预解释完成并返回结果
        tasks.joinAll()
        return@async stream
    }

    /**
     * 静态物品流
     *
     * @param replenish 每获取一次补充一次 (执行一次 [generateStaticStream])
     */
    val staticStreamGenerating by replenish { generateStaticStream() }

    /**
     * 异步生成物品
     */
    fun buildAsync(context: ArgumentContext) = ItemElement.scope.async {
        // 获取物品流
        val stream = staticStreamGenerating.await()
        // 更新上下文
        stream.context = context

        // 呼出开始生成的事件
        ItemGenerateEvent.Pre(stream.identifier, this@DefaultGenerator, context, origin.property).call()

        // 处理物品流
        processStream(stream, this)

        // 呼出生成结束的事件
        val event = ItemGenerateEvent.Post(stream.identifier, this@DefaultGenerator, context, stream.item)
        event.call()
        event.item // 返回最终结果
    }.asCompletableFuture()

    /**
     * 生成静态物品流
     */
    fun generateStaticStream(): Deferred<NativeItemStream> = ItemElement.scope.async {
        val stream = baseStream.await().copyWith(SimpleContext())
        // 获取静态配置
        val property = stream.fetchElement() as? JsonObject
        val staticProperty = property?.get("static") ?: return@async stream // 没有就算了, 前面走个拷贝就走
        val delected = staticProperty is JsonPrimitive && staticProperty.booleanOrNull == true // static 是否为 true
        // 替换物品流的元素内容
        if (!delected) stream.updateElement(staticProperty)
        // 好戏开场: 处理静态物品流
        processStream(stream, this).join()
        if (delected) stream.updateElement(JsonObject(HashMap()))
        // 处理完了返回就是了
        return@async stream
    }

    /**
     * 处理物品流 (解释 -> 序列化)
     */
    fun processStream(stream: ItemStream, scope: CoroutineScope) = scope.launch {
        // 解释器解释元素
        val interpreterTasks = ItemRegistry.interpreters
            .filter {
                val anno = getPreInterpretable(it)
                anno == null || !anno.onlyPre
            } // 上面处理过了
            .map {
                launch {
                    measureTimeMillis {
                        it.interpret(stream)
                    }.let { t -> debug("[TIME MARK] $it costs $t ms.") }
                }
            }

        // 序列化任务需要完全在解释后, 故等待解释任务的完成
        interpreterTasks.joinAll()

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

    /**
     * 判断一个 [ItemInterpreter] 是不是 可预解释的
     */
    private fun getPreInterpretable(interpreter: ItemInterpreter): ItemInterpreter.PreInterpretable? {
        return interpreter::class.java.getAnnotationIfPresent(ItemInterpreter.PreInterpretable::class.java)
    }

}