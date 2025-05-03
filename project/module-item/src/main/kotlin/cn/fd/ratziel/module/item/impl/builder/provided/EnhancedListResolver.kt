package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.containsNonEscaped
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

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
        val list = ArrayList<JsonElement>()
        for (child in node.value) {
            // 要求列表内的所有元素都是 JsonPrimitive
            if (child !is JsonTree.PrimitiveNode) return
            // 仅处理字符串类型
            if (!child.value.isString) continue
            // 获取内容
            val content = child.value.contentOrNull ?: continue
            // 分割换行符
            val split = content.splitNonEscaped("\\n", "{nl}", ignoreCase = true)
            for (line in split) {
                // 跳过含有删行符的
                if (line.containsNonEscaped("{dl}")) continue
                // 去除转义: 替换 "\{dl}" 为 "{dl}"
                val stripped = line.replace("\\{dl}", "{dl}")
                // 添加
                list.add(JsonPrimitive(stripped))
            }
        }
    }

}