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
import taboolib.common.platform.function.warning
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

    override val resolvers = arrayOf<Priority<ItemResolver>>(DefaultItemResolver().priority())

    override val serializers = arrayOf<Priority<ItemSerializer<*>>>(DefaultItemSerializer(json).priority())

    /**
     * 解析
     */
    fun resolve(element: JsonElement) = CompletableFuture<JsonObject>().also { future ->
        ConcurrentHashMap<String, JsonElement>().also { resolving ->
            FutureFactory {
                resolvers.sortPriority().forEach {
                    supplyAsync {
                        try {
                            it.resolve(element).also { resolved ->
                                (resolved as? JsonObject)?.forEach {
                                    resolving[it.key] = it.value
                                }
                            }
                        } catch (ex: Exception) {
                            warning("Failed to resolve element!")
                            ex.printStackTrace()
                        }
                    }
                }
            }.whenAllComplete { future.complete(JsonObject(resolving)) }
        }
    }

    /**
     * 序列化
     */
    fun serialize(element: JsonElement) = CompletableFuture<List<ItemComponent>>().also { future ->
        LinkedList<ItemComponent>().also { serializing ->
            FutureFactory {
                serializers.sortPriority().forEach {
                    supplyAsync {
                        try {
                            serializing.add(it.deserialize(element))
                        } catch (ex: Exception) {
                            warning("Failed to serialize element!")
                            ex.printStackTrace()
                        }
                    }
                }
            }.whenAllComplete { future.complete(serializing) }
        }
    }

    /**
     * 转换
     */
    fun transform(components: List<ItemComponent>) = CompletableFuture<List<NBTCompound>>().also { future ->
        LinkedList<NBTCompound>().also { transforming ->
            FutureFactory {
                components.forEach {
                    supplyAsync {
                        try {
                            transforming.add(NBTCompound().also { data -> it.detransform(data) })
                        } catch (ex: Exception) {
                            warning("Failed to serialize element!")
                            ex.printStackTrace()
                        }
                    }
                }
            }.whenAllComplete { future.complete(transforming) }
        }
    }

    fun build() {
        val jsonObject = resolve(origin.property).get()
        println(jsonObject)
        val components = serialize(jsonObject).get()
        println(components)
        try {
            println(transform(components).get())
        } catch (ex: Exception) {
            warning("Failed to build element!")
            ex.printStackTrace()
        }
    }

}