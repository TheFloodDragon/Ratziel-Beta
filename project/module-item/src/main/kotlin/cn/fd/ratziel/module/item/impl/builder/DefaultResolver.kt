package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.JsonHandler
import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * NativeItemResolver
 *
 * @author TheFloodDragon
 * @since 2024/8/13 10:54
 */
object DefaultResolver : ItemResolver {

    /**
     * [SectionResolver] 应该访问的节点
     */
    val visitNodes: Array<String> get() = DefaultSerializer.usedNodes

    /**
     * 解析元素
     *
     * @return 解析完后, 返回值只包含 [visitNodes] 内的节点, 因此 [DefaultResolver] 需要作为最后一个解析
     */
    override fun resolve(element: JsonElement, context: ArgumentContext): JsonElement {
        if (element !is JsonObject) return resolveBySection(element, context)
        // 过滤节点
        val builder: MutableMap<String, JsonElement> = HashMap()
        for ((node, value) in element) {
            if (visitNodes.contains(node)) builder[node] = resolveBySection(value, context)
        }
        return JsonObject(builder)
    }

    fun resolveBySection(element: JsonElement, context: ArgumentContext): JsonElement {
        return JsonHandler.mapPrimitives(element) { SectionResolver.resolve(it, context) }
    }

}