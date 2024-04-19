package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.FutureFactory
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.core.util.priority
import cn.fd.ratziel.core.util.sortPriority
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
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

    override val serializers = arrayOf<Priority<ItemSerializer<*, *>>>(DefaultItemSerializer(json).priority())

    /**
     * 解析
     */
    fun resolve(): CompletableFuture<JsonObject> = CompletableFuture<JsonObject>().also { future ->
        ConcurrentHashMap<String, JsonElement>().also { resolving ->
            FutureFactory {
                resolvers.sortPriority().forEach {
                    supplyAsync {
                        it.resolve(origin.property).also { resolved ->
                            (resolved as? JsonObject)?.forEach { key, value ->
                                resolving.merge(key, value) { _, _ -> value }
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
    fun serialize() {
        TODO("妈的")
    }

    fun build() {
        resolve().get()
    }

}