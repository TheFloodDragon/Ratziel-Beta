package cn.fd.ratziel.module.item.impl.builder.resolver

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.serialization.asMutable
import cn.fd.ratziel.core.serialization.mapPrimitives
import cn.fd.ratziel.core.util.priority
import cn.fd.ratziel.core.util.sortPriority
import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.SectionResolver
import cn.fd.ratziel.module.item.impl.builder.CommonItemSerializer
import cn.fd.ratziel.module.item.impl.builder.resolver.sectionResolvers.PapiResolver
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.function.Function

/**
 * BasicItemResolver - 基础解析器
 *
 * @author TheFloodDragon
 * @since 2024/5/24 21:32
 */
@Deprecated("see CommonItemResolver")
object BasicItemResolver : ItemResolver {

    /**
     * 可通过的节点 (不在此的节点都会被过滤掉)
     */
    val accessibleNodes: MutableSet<String> = CommonItemSerializer.usedNodes.toMutableSet()

    /**
     * 字符串解析器
     */
    val resolvers: MutableList<Priority<SectionResolver.StringResolver>> = mutableListOf(
        PapiResolver priority -1,
        BasicTagResolver priority 99,
    )

    override fun resolve(element: JsonElement, context: ArgumentContext): JsonElement =
        // 过滤节点
        CleanUp.handleOnFilter(element, accessibleNodes) { filtered ->
            // 处理 JsonPrimitive
            filtered.value.mapPrimitives { resolvePrimitive(it, context) }
        }

    fun resolvePrimitive(element: JsonPrimitive, context: ArgumentContext): JsonElement {
        var handle = element.content
        // 遍历字符串解析器处理
        for (resolver in resolvers.sortPriority()) {
            handle = resolver.resolve(handle, context)
        }
        return JsonPrimitive(handle)
    }

    object CleanUp {

        @JvmStatic
        fun handleOnFilter(
            element: JsonObject,
            accessibleNodes: Iterable<String>,
            action: Function<Map.Entry<String, JsonElement>, JsonElement>
        ): JsonObject {
            val builder = element.asMutable()
            for (entry in builder) {
                // 在允许的节点范围内
                if (entry.key in accessibleNodes) {
                    builder[entry.key] = action.apply(entry) // 通过操作替换节点
                }
            }
            return builder.asImmutable()
        }

        @JvmStatic
        fun handleOnFilter(
            element: JsonElement,
            accessibleNodes: Iterable<String>,
            action: Function<Map.Entry<String, JsonElement>, JsonElement>
        ): JsonElement =
            if (element is JsonObject) handleOnFilter(element, accessibleNodes, action) else element

    }

}