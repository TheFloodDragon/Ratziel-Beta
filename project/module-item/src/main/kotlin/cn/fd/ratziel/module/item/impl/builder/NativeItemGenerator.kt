package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.core.util.FutureFactory
import cn.fd.ratziel.core.util.priority
import cn.fd.ratziel.core.util.sortPriority
import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.impl.RatzielItem
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import taboolib.common.platform.function.severe
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

/**
 * NativeItemGenerator
 *
 * @author TheFloodDragon
 * @since 2024/4/13 17:34
 */
class NativeItemGenerator(override val origin: Element) : ItemGenerator {

    val json: Json by lazy {
        Json(baseJson) {}
    }

    val executor by lazy {
        Executors.newFixedThreadPool(8)
    }

    override val serializers = mutableListOf(DefaultItemSerializer(json))

    override val resolvers: MutableList<Priority<ItemResolver>> = mutableListOf(DefaultItemResolver().priority())

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
        return element
    }

    /**
     * 序列化 (并行)
     */
    fun serialize(element: JsonElement): FutureFactory<ItemComponent?> = FutureFactory {
        for (serializer in serializers) {
            submitAsync(executor) {
                try {
                    serializer.deserialize(element)
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
    fun transform(data: ItemData, components: Iterable<ItemComponent>) {
        for (component in components) {
            try {
                component.transform(data)
            } catch (ex: Exception) {
                severe("Failed to transform component: $component!")
                ex.printStackTrace()
            }
        }
    }

    override fun build(arguments: ArgumentFactory): CompletableFuture<out NeoItem> {
        // 解析成 JsonElement
        val element = resolve(origin.property, arguments)
        // 并行序列化并收集结果
        val serializeTask = serialize(element)
        // 合成最终产物
        return serializeTask.thenApply { components ->
            val data = ItemData()
            transform(data, components.mapNotNull { it })
            RatzielItem(data)
        }
    }

}