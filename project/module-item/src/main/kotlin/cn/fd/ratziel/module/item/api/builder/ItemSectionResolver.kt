package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

/**
 * ItemSectionResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 19:16
 */
interface ItemSectionResolver {

    /**
     * 准备解析阶段处理
     */
    fun prepare(node: JsonTree.Node, context: ArgumentContext) = Unit

    /**
     * 解析处理 [JsonTree.Node] (所有类型的节点)
     *
     * @param node 要解析处理节点
     * @param context 上下文
     */
    fun resolve(node: JsonTree.Node, context: ArgumentContext) = Unit

    /**
     * 直接尝试获取字符串部分
     */
    fun JsonTree.Node.stringSection(): JsonTree.PrimitiveNode? {
        return (this as? JsonTree.PrimitiveNode)?.takeIf { isValidSection(it.value) }
    }

    companion object {

        /**
         * 判断部分的有效性
         */
        @JvmStatic
        fun isValidSection(element: JsonElement): Boolean {
            return element is JsonPrimitive && element.isString && element !is JsonNull
        }

    }

}