package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.FutureFactory
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.core.util.printOnException
import cn.fd.ratziel.core.util.priority
import cn.fd.ratziel.core.util.sortPriority
import cn.fd.ratziel.module.item.api.ItemComponent
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.nbt.NBTCompound
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.util.*
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

    override val serializers = arrayOf<Priority<ItemSerializer<*>>>(DefaultItemSerializer(json).priority())

    override val resolvers = arrayOf<Priority<ItemResolver>>(DefaultItemResolver().priority())

    /**
     * 解析
     */
    fun resolve(element: JsonElement) = CompletableFuture<JsonObject>().also { future ->
        val resolving = ConcurrentHashMap<String, JsonElement>()
        FutureFactory {
            resolvers.sortPriority().forEach {
                supplyAsync(executor) {
                    it.resolve(element).also { resolved ->
                        (resolved as? JsonObject)?.forEach {
                            resolving[it.key] = it.value
                        }
                    }
                }.printOnException().submit()
            }
        }.whenAllComplete { future.complete(JsonObject(resolving)) }
    }

    /**
     * 序列化
     */
    fun serialize(element: JsonElement) = CompletableFuture<List<ItemComponent>>().also { future ->
        val serializing = LinkedList<ItemComponent>()
        FutureFactory {
            serializers.sortPriority().forEach {
                supplyAsync(executor) {
                    serializing.add(it.deserialize(element))
                }.printOnException().submit()
            }
        }.whenAllComplete { future.complete(serializing) }
    }

    /**
     * 转换
     */
    fun transform(components: List<ItemComponent>) = CompletableFuture<List<NBTCompound>>().also { future ->
        val transforming = LinkedList<NBTCompound>()
        FutureFactory {
            components.forEach {
                supplyAsync(executor) {
                    transforming.add(it.transform())
                }.printOnException().submit()
            }
        }.whenAllComplete { future.complete(transforming) }
    }

    fun build() {
        val jsonObject = resolve(origin.property).get()
        val components = serialize(jsonObject).get()
        println(components)
        println(transform(components).get())
    }

}