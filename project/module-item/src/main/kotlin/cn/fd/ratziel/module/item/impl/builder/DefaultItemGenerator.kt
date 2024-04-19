package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.FutureFactory
import cn.fd.ratziel.core.serialization.baseJson
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

    override val resolvers = arrayOf<Priority<ItemResolver>>()

    override val serializers = arrayOf<Priority<ItemSerializer<*>>>(DefaultItemSerializer(json).priority())

    /**
     * 解析
     */
    fun resolve(element: JsonElement) = CompletableFuture<JsonObject>().also { future ->
        ConcurrentHashMap<String, JsonElement>().also { resolving ->
            FutureFactory {
                resolvers.sortPriority().forEach {
                    supplyAsync {
                        it.resolve(element).also { resolved ->
                            (resolved as? JsonObject)?.forEach {
                                resolving[it.key] = it.value
                            }
                        }
                    }
                }
            }.whenAllComplete { future.complete(JsonObject(resolving)) }
        }
    }

    /**
     * 序列化
     */
    fun serialize(element: JsonElement) = CompletableFuture<List<ItemComponent<*>>>().also { future ->
        LinkedList<ItemComponent<*>>().also { serializing ->
            FutureFactory {
                serializers.sortPriority().forEach {
                    supplyAsync {
                        serializing.add(it.deserialize(element) as ItemComponent<*>)
                    }
                }
            }.whenAllComplete { future.complete(serializing) }
        }
    }

    /**
     * 转换
     */
    fun transform(components: List<ItemComponent<in Any>>) = CompletableFuture<List<NBTCompound>>().also { future ->
        LinkedList<NBTCompound>().also { transforming ->
            FutureFactory {
                components.forEach {
                    supplyAsync {
                        transforming.add(NBTCompound().also { data -> it.transformer().detransform(it, data) })
                    }
                }
            }.whenAllComplete { future.complete(transforming) }
        }
    }

    fun build() {
        resolve(origin.property).thenApply { jsonObject ->
            serialize(jsonObject).thenApply { components ->
                @Suppress("UNCHECKED_CAST")
                transform(components as List<ItemComponent<in Any>>)
            }
        }.get().get().get().let {
            println(it)
        }
    }

}