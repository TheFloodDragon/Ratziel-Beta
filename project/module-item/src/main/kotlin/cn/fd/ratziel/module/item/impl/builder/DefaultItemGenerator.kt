package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.util.FutureFactory
import cn.fd.ratziel.core.util.priority
import cn.fd.ratziel.core.util.sortPriority
import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.TheItemData
import cn.fd.ratziel.module.item.impl.component.ItemDisplay
import cn.fd.ratziel.module.item.util.toApexDataUncheck
import taboolib.common.platform.function.severe
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArraySet

/**
 * DefaultItemGenerator
 *
 * @author TheFloodDragon
 * @since 2024/4/13 17:34
 */
object DefaultItemGenerator : ItemGenerator {

    /**
     * 物品序列化器
     * 注意: 序列化没有优先, 序列化器的优先级会传递到物品转换阶段
     */
    val serializers = CopyOnWriteArraySet<ItemSerializer<*>>().apply { add(DefaultItemSerializer) }

    /**
     * 物品解析器
     */
    val resolvers = CopyOnWriteArraySet<Priority<ItemResolver>>().apply {
        add(BasicItemResolver.priority())
        add(BasicItemResolver.CleanUp priority Byte.MAX_VALUE)// 最后清除
    }

    /**
     * 物品转换器
     */
    val transformers = HashMap<Class<*>, Priority<ItemTransformer<*>>>().apply {
        put(ItemDisplay::class.java, ItemDisplay.priority())
    }

//    /**
//     * 解析
//     */
//    fun resolve(element: JsonElement, arguments: ArgumentFactory): JsonElement {
//        var result = element
//        for (resolver in resolvers.sortPriority()) {
//            try {
//                result = resolver.resolve(result, arguments)
//            } catch (ex: Exception) {
//                severe("Failed to resolve element by $resolver!")
//                ex.printStackTrace()
//            }
//        }
//        return result
//    }
//
//    /**
//     * 序列化 (并行)
//     * 传递优先级至 [transform] 阶段
//     */
//    fun serialize(element: JsonElement)= FutureFactory<List<Any>> {
//        for (serializer in serializers) {
//            submitAsync(ItemElement.executor) {
//                try {
//                    serializer.deserialize(element)
//                } catch (ex: Exception) {
//                    severe("Failed to deserialize element by $serializer!")
//                    ex.printStackTrace(); null
//                }
//            }
//        }
//    }
//
//    /**
//     * 转换
//     */
//    fun transform( components: Iterable<Any>) = FutureFactory<List<ItemData>> {
//        for (transformer in transformers) {
//            val component = components.find { it::class.java.isAssignableFrom(transformer.value.type) }
//            try {
//                if (component != null) {
//                    transformer.value.transform(uncheck(component))
//                }
//            } catch (ex: Exception) {
//                severe("Failed to transform component: $component!")
//                ex.printStackTrace()
//            }
//        }
//    }

    fun build(origin: Element, data: ItemData, arguments: ArgumentFactory): CompletableFuture<RatzielItem> {
        var element = origin.property
        // Resolve
        for (resolver in resolvers.sortPriority()) {
            try {
                element = resolver.resolve(element, arguments)
            } catch (ex: Exception) {
                severe("Failed to resolve element by $resolver!")
                ex.printStackTrace()
            }
        }
        // S
        val serializeFactory = FutureFactory<Any?>()
        val transformFactory = FutureFactory<ItemData?>()

        for (serializer in serializers) {
            val serializeTask = serializeFactory.submitAsync(ItemElement.executor) {
                try {
                    serializer.deserialize(element)
                } catch (ex: Exception) {
                    severe("Failed to deserialize element by $serializer!")
                    ex.printStackTrace(); null
                }
            }
            //
            val transformTask = serializeTask.thenApply {
                if (it == null) return@thenApply null
                val transformer = (transformers[it::class.java] ?: return@thenApply null).value
                transformer.toApexDataUncheck(it)
            }
            transformFactory.submitTask(transformTask)
        }

        return transformFactory.thenApply { list ->
            list.forEach {
                if (it != null) TheItemData.mergeShallow(data, it, true)
            }
            RatzielItem(data)
        }
    }

    override fun build(origin: Element, arguments: ArgumentFactory) = build(origin, TheItemData(), arguments)

}