package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.util.FutureFactory
import cn.fd.ratziel.core.util.priority
import cn.fd.ratziel.core.util.sortPriority
import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.impl.TheItemData
import cn.fd.ratziel.module.item.impl.RatzielItem
import kotlinx.serialization.json.JsonElement
import taboolib.common.platform.function.severe
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArraySet

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

    /**
     * 物品序列化器
     * 注意: 序列化没有优先, 序列化器的优先级会传递到物品转换阶段
     */
    val serializers: MutableSet<Priority<ItemSerializer<*>>> =
        CopyOnWriteArraySet(listOf(DefaultItemSerializer().priority()))

    /**
     * 物品解析器
     */
    val resolvers: MutableSet<Priority<ItemResolver>> =
        CopyOnWriteArraySet(listOf(
            BasicItemResolver.priority(),
            BasicItemResolver.CleanUp priority Byte.MAX_VALUE // 最后清除
        ))

    /**
     * 解析
     */
    fun resolve(element: JsonElement, arguments: ArgumentFactory): JsonElement {
        var result = element
        for (resolver in resolvers.sortPriority()) {
            try {
                result = resolver.resolve(result, arguments)
            } catch (ex: Exception) {
                severe("Failed to resolve element by $resolver!")
                ex.printStackTrace()
            }
        }
        return result
    }

    /**
     * 序列化 (并行)
     * 传递优先级至 [transform] 阶段
     */
    fun serialize(element: JsonElement): FutureFactory<Priority<ItemComponent>?> = FutureFactory {
        for (serializer in serializers) {
            submitAsync(ItemElement.executor) {
                try {
                    serializer.value.deserialize(element) priority serializer.priority
                } catch (ex: Exception) {
                    severe("Failed to deserialize element by $serializer!")
                    ex.printStackTrace(); null
                }
            }
        }
    }

    /**
     * 转换
     */
    fun transform(data: ItemData, components: Iterable<Priority<ItemComponent>>) {
        for (component in components.sortPriority()) {
            try {
                component.transform(data)
            } catch (ex: Exception) {
                severe("Failed to transform component: $component!")
                ex.printStackTrace()
            }
        }
    }

    fun build(data: ItemData, arguments: ArgumentFactory): CompletableFuture<RatzielItem> {
        // 解析成 JsonElement
        val element = resolve(origin.property, arguments)
        // 并行序列化并收集结果
        val serializeTask = serialize(element)
        // 合成最终产物
        return serializeTask.thenApply { components ->
            transform(data, components.mapNotNull { it })
            RatzielItem(data)
        }
    }

    override fun build(arguments: ArgumentFactory) = build(TheItemData(), arguments)

}