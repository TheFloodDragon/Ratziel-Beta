package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.containsNonEscaped
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

/**
 * EnhancedListResolver - 增强列表解析器
 *
 * @author TheFloodDragon
 * @since 2025/5/3 19:20
 */
object EnhancedListResolver : ItemSectionResolver {

    const val NEWLINE = "\\n"
    const val NEWLINE_2 = "{nl}"
    const val DEL_LINE = "{dl}"

    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        if (node !is JsonTree.ArrayNode) return
        // 重新构建列表
        val list = ArrayList<JsonTree.PrimitiveNode>()
        for (child in node.value) {
            // 要求列表内的所有元素都是 JsonPrimitive
            if (child !is JsonTree.PrimitiveNode) return
            // 分割换行符
            val split = child.value.content.splitNonEscaped(NEWLINE, NEWLINE_2, ignoreCase = true)
            for (line in split) {
                // 跳过含有删行符的
                if (line.containsNonEscaped(DEL_LINE)) continue
                // 去除转义: 替换 "\{dl}" 为 "{dl}"
                val stripped = line.replace("\\" + DEL_LINE, DEL_LINE)
                // 添加
                list.add(JsonTree.PrimitiveNode(JsonPrimitive(stripped), node))
            }
        }
        node.value = list // 替换列表
    }

}