package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import kotlinx.serialization.json.JsonPrimitive

/**
 * EnhancedListResolver - 增强列表解析器
 *
 * @author TheFloodDragon
 * @since 2025/5/3 19:20
 */
object EnhancedListResolver : ItemSectionResolver {

    const val NEWLINE = "\\n"

    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        if (node !is JsonTree.ArrayNode) return
        // 重新构建列表
        val list = ArrayList<JsonTree.PrimitiveNode>()
        for (child in node.value) {
            // 要求列表内的所有元素都是 JsonPrimitive
            if (child !is JsonTree.PrimitiveNode) return
            // 分割换行符
            val split = child.value.content.splitNonEscaped(NEWLINE)
            for (line in split) {
                list.add(JsonTree.PrimitiveNode(JsonPrimitive(line), node))
            }
        }
        node.value = list // 替换列表
    }

}