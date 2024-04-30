package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.util.FutureFactory
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.core.util.printOnException
import cn.fd.ratziel.core.util.priority
import cn.fd.ratziel.core.util.sortPriority
import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * DefaultItemGenerator
 *
 * @author TheFloodDragon
 * @since 2024/4/13 17:34
 */
class DefaultItemGenerator(override val origin: Element) : ItemGenerator {

    val json: Json by lazy {
        Json(baseJson) {}
    }

    val executor by lazy {
        Executors.newFixedThreadPool(8)
    }

    override val serializers: List<Priority<ItemSerializer<*>>> by lazy {
        listOf(DefaultItemSerializer(json).priority())
    }

    override val resolvers: List<Priority<ItemResolver>> by lazy {
        listOf(DefaultItemResolver().priority())
    }

    /**
     * 解析
     */
    fun resolve(element: JsonElement): CompletableFuture<JsonObject> = ConcurrentHashMap<String, JsonElement>().let { map ->
        FutureFactory {
            resolvers.sortPriority().forEach {
                supplyAsync(executor) {
                    it.resolve(element).also { resolved ->
                        (resolved as? JsonObject)?.forEach {
                            map[it.key] = it.value
                        }
                    }
                }.printOnException().submit()
            }
        }.whenComplete<JsonObject> { JsonObject(map) }
    }

    /**
     * 序列化
     */
    fun serialize(element: JsonElement): CompletableFuture<List<ItemComponent>> =
        FutureFactory {
            serializers.sortPriority().forEach {
                supplyAsync(executor) {
                    it.deserialize(element)
                }.printOnException().submit()
            }
        }.whenComplete()

    /**
     * 转换
     */
    fun transform(components: List<ItemComponent>): CompletableFuture<List<ItemData>> =
        FutureFactory {
            components.forEach {
                supplyAsync(executor) {
                    it.transform()
                }.printOnException().submit()
            }
        }.whenComplete()

    fun build() {
        val jsonObject = resolve(origin.property).get()
        val components = serialize(jsonObject).get()
        println(components)
        println(transform(components).get())
    }

}