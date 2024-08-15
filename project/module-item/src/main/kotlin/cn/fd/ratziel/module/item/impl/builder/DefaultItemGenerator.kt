package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.IdentifierImpl
import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.util.FutureFactory
import cn.fd.ratziel.core.util.sortPriority
import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.function.uncheck
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.api.builder.ItemTransformer
import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import cn.fd.ratziel.module.item.api.event.ItemResolveEvent
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.SimpleItemData
import kotlinx.serialization.json.JsonElement
import taboolib.common.platform.function.severe
import java.util.concurrent.CompletableFuture

/**
 * DefaultItemGenerator
 *
 * @author TheFloodDragon
 * @since 2024/4/13 17:34
 */
class DefaultItemGenerator(
    /**
     * 原始物品配置 (元素)
     */
    val origin: Element
) : ItemGenerator {

    fun build(sourceData: ItemData, context: ArgumentContext): CompletableFuture<NeoItem> {
        // 生成物品唯一标识符
        val identifier = IdentifierImpl(origin.name)

        // PreEvent
        ItemGenerateEvent.Pre(identifier, this, context).call()

        // Step1: Resolve (with priorities)
        val element = resolve(origin.property, context, ItemRegistry.Resolver.getResolversSorted())
        // ResolvedEvent
        ItemResolveEvent(identifier, element, context).call()

        // Step2: Serialize (async)
        val serializeFactory = FutureFactory<Any?>()

        // Step3: Transform (async)
        val transformFactory = FutureFactory<Priority<ItemData>?>()

        // 遍历所有注册的序列化器
        for (serializer in ItemRegistry.Serializer.getSerializers()) {
            // 创建序列化任务 (提交至serializeFactory)
            val serializeTask = createSerializeTask(element, serializer).also { serializeFactory.submitTask(it) } // 提交序列化任务
            // 创建转换任务 (在序列化任务完成时执行)
            createTransformTask(serializeTask).also { transformFactory.submitTask(it) } // 提交转换任务
        }

        // Step4: Merge all (sync 串行)
        return transformFactory.thenApply { results ->
            // 优先级排列 (优先级低的在前面)
            for (data in results.mapNotNull { it }.sortPriority().reversed()) {
                SimpleItemData.merge(sourceData, data, true) // 合并数据
            }
            // 合成最终结果
            val info = RatzielItem.Info(identifier, origin.property.hashCode())
            val item = RatzielItem(info, sourceData)
            // PostEvent
            ItemGenerateEvent.Post(item.id, this, item, context)
                .also { it.call() }.item
        }
    }

    override fun build(context: ArgumentContext) = build(SimpleItemData(), context)

    private fun resolve(element: JsonElement, context: ArgumentContext, resolvers: List<ItemResolver>): JsonElement {
        var result = element
        for (resolver in resolvers) {
            try {
                result = resolver.resolve(result, context)
            } catch (ex: Exception) {
                severe("Failed to resolve element by $resolver!")
                ex.printStackTrace()
            }
        }
        return result
    }

    private fun createSerializeTask(element: JsonElement, serializer: ItemSerializer<*>): CompletableFuture<Any?> = CompletableFuture.supplyAsync({
        try {
            serializer.deserialize(element)
        } catch (ex: Exception) {
            severe("Failed to deserialize element by \"$serializer!\"! Target element: $element")
            ex.printStackTrace(); null
        }
    }, ItemElement.executor)

    private fun createTransformTask(serializeTask: CompletableFuture<Any?>): CompletableFuture<Priority<ItemData>?> =
        serializeTask.thenApplyAsync({ component ->
            // 若组件未空, 则不进行转换
            if (component == null) return@thenApplyAsync null
            // 获取转换器
            val prt = ItemRegistry.Component.getPriority(component::class.java) // 用于传递优先级
            val transformer = prt?.value
            if (transformer == null) {
                severe("Cannot find transformer for component \"$component\"!")
                return@thenApplyAsync null
            }
            // 转换成以顶级节点为根节点的数据
            try {
                val data = SimpleItemData()
                (uncheck<ItemTransformer<Any>>(transformer)).transform(data, component)
                Priority(prt.priority, data) // 封装成优先级对象后传递给合并阶段
            } catch (ex: Exception) {
                severe("Failed to transform component by \"$transformer\"! Target component: $component")
                ex.printStackTrace(); null
            }
        }, ItemElement.executor)

}