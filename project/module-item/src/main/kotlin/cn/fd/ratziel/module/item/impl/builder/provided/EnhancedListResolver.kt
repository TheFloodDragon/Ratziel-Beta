package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.containsNonEscaped
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

/**
 * EnhancedListResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 19:20
 */
object EnhancedListResolver : ItemSectionResolver {

    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        if (node !is JsonTree.ArrayNode) return
        // 重新构建列表
        val list = ArrayList<JsonTree.Node>()
        for (child in node.value) {
            // 要求列表内的所有元素都是 JsonPrimitive
            if (child !is JsonTree.PrimitiveNode) return
            // 仅处理字符串类型
            if (!child.value.isString || child.value !is JsonNull) continue
            // 分割换行符
            val split = child.value.content.splitNonEscaped("\\n", "{nl}", ignoreCase = true)
            for (line in split) {
                // 跳过含有删行符的
                if (line.containsNonEscaped("{dl}")) continue
                // 去除转义: 替换 "\{dl}" 为 "{dl}"
                val stripped = line.replace("\\{dl}", "{dl}")
                // 添加
                list.add(JsonTree.PrimitiveNode(JsonPrimitive(stripped), node))
            }
        }
        node.value = list // 替换列表
    }

}