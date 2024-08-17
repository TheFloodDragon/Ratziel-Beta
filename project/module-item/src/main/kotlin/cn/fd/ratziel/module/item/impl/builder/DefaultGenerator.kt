package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.IdentifierImpl
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.util.digest
import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import cn.fd.ratziel.module.item.api.event.ItemResolvedEvent
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.SimpleData
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.serialization.json.JsonElement
import taboolib.common.platform.function.severe
import java.util.concurrent.CompletableFuture

/**
 * DefaultItemGenerator
 *
 * @author TheFloodDragon
 * @since 2024/4/13 17:34
 */
class DefaultGenerator(
    /**
     * 原始物品配置 (元素)
     */
    val origin: Element
) : ItemGenerator {

    /**
     * 构建物品
     */
    override fun build(context: ArgumentContext) = buildAsync(SimpleData(), context)

    /**
     * 异步生成物品
     *
     * 注: 此处使用多线程进行 反序列化和应用数据 的操作
     * 因为 [ItemData.tag] 默认是使用一个线程安全的 [Map]
     * 开发过程中也尽可能避免将其换成 非线程安全的
     * 同时由于 NBT数据 的处理 不再依赖原版的, 而是独立实现的
     * 所以通过 原版物品 转换到此的 [ItemData.tag] 也是线程安全的
     */
    fun buildAsync(data: ItemData, context: ArgumentContext): CompletableFuture<NeoItem> {
        // 获取物品唯一标识符
        val identifier = IdentifierImpl(origin.name)

        // 呼出开始生成的事件
        ItemGenerateEvent.Pre(identifier, this, context).call()

        // 解析元素 (同步)
        val resolved = resolve(identifier, origin.property, context)

        // 获取序列化器列表
        val serializers = ItemRegistry.Serializer.getSerializers()

        // 处理最终结果 (异步)
        return ItemElement.scope.async {
            // 生成任务列表
            val tasks: List<Job> = serializers.map { createTask(this@async, it, resolved, data) }
            val extendTasks: List<Job>? = ItemGenerateEvent.TaskAdder().takeUnless { it.call() }
                ?.extendTasks?.map { launch { it.accept(resolved, data) } }
            // 等待所有任务完成
            tasks.joinAll()
            extendTasks?.joinAll()
            // 合成最终结果
            val version = origin.property.toString().digest()
            val item = RatzielItem.of(RatzielItem.Info(identifier, version), data)
            // 呼出生成结束的事件
            val event = ItemGenerateEvent.Post(item.id, this@DefaultGenerator, item, context)
            event.call()
            event.item // 返回最终结果
        }.asCompletableFuture()
    }

    /**
     * 创建任务
     * 流程: 反序列化 -> 转化数据 (应用到 [sourceData])
     */
    fun createTask(
        scope: CoroutineScope,
        serializer: ItemSerializer<*>,
        resolved: JsonElement,
        sourceData: ItemData
    ) = scope.launch {
        val component = deserialize(serializer, resolved) ?: return@launch
        transform(component, sourceData)
    }

    fun deserialize(
        serializer: ItemSerializer<*>,
        resolved: JsonElement
    ): Any? {
        // 反序列成物品组件
        try {
            return serializer.deserialize(resolved)
        } catch (ex: Exception) {
            severe("Failed to deserialize element by \"$serializer!\"! Target element: $resolved")
            ex.printStackTrace()
        }
        return null
    }

    fun transform(
        component: Any,
        sourceData: ItemData
    ) {
        // 获取转化器
        val transformer = ItemRegistry.Component.getUnsafe(component::class.java)
        if (transformer == null) {
            severe("Transformer not found for component \"$component\"!")
            return
        }
        try {
            // 应用数据
            transformer.transform(sourceData, component)
        } catch (ex: Exception) {
            severe("Failed to transform component by \"$transformer\"! Target component: $component")
            ex.printStackTrace()
        }
    }

    /**
     * 物品解析实现部分
     * 结束后呼出事件 [ItemResolvedEvent]
     */
    fun resolve(identifier: Identifier, element: JsonElement, context: ArgumentContext): JsonElement {
        var result = element
        // 解析器列表 (确保最后一个是默认解析器)
        val resolvers = ItemRegistry.Resolver.getResolversSorted()
            .plus(DefaultResolver)
        // 使用解析器解析
        for (resolver in resolvers) {
            try {
                result = resolver.resolve(result, context)
            } catch (ex: Exception) {
                severe("Failed to resolve element by $resolver!")
                ex.printStackTrace()
            }
        }
        // 呼出事件
        ItemResolvedEvent(identifier, element).call()
        // 返回结果
        return result
    }

}