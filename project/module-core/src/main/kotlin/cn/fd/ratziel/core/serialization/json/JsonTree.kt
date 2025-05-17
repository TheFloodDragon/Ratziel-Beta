package cn.fd.ratziel.core.serialization.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.function.Consumer

/**
 * JsonTree
 *
 * @author TheFloodDragon
 * @since 2025/5/3 17:22
 */
class JsonTree(
    /**
     * 根节点
     */
    val root: Node
) {

    constructor(element: JsonElement) : this(parseToNode(element, null))

    /**
     * 转化成 [JsonElement]
     */
    fun toElement(): JsonElement {
        return parseToElement(root)
    }

    /**
     * Node
     */
    sealed interface Node {

        /**
         * 父节点 (空代表此节点为跟节点)
         */
        val parent: Node?

    }

    /**
     * ObjectNode
     */
    class ObjectNode(
        var value: Map<String, Node>,
        override val parent: Node?,
    ) : Node

    /**
     * ArrayNode
     */
    class ArrayNode(
        var value: List<Node>,
        override val parent: Node?,
    ) : Node

    /**
     * PrimitiveNode
     */
    class PrimitiveNode(
        var value: JsonPrimitive,
        override val parent: Node?,
    ) : Node

    companion object {

        /**
         * 展开节点并进行处理
         *
         * @param node 要展开的节点
         * @param action 处理动作
         */
        @JvmStatic
        fun unfold(node: Node, action: Consumer<Node>) {
            action.accept(node) // 先执行动作
            when (node) {
                is ObjectNode -> node.value.forEach { unfold(it.value, action) }
                is ArrayNode -> node.value.forEach { unfold(it, action) }
                is PrimitiveNode -> return // 无法继续展开
            }
        }

        /**
         * 将 [JsonElement] 解析成节点
         */
        @JvmStatic
        @JvmOverloads
        fun parseToNode(element: JsonElement, parent: Node? = null): Node = when (element) {
            is JsonObject -> ObjectNode(emptyMap(), parent).apply { value = element.mapValues { parseToNode(it.value, this) } }
            is JsonArray -> ArrayNode(emptyList(), parent).apply { value = element.map { parseToNode(it, this) } }
            is JsonPrimitive -> PrimitiveNode(element, parent) // 无法继续展开
        }

        /**
         * 将节点 解析成 [JsonElement]
         */
        @JvmStatic
        fun parseToElement(node: Node): JsonElement = when (node) {
            is ObjectNode -> JsonObject(node.value.mapValues { parseToElement(it.value) })
            is ArrayNode -> JsonArray(node.value.map { parseToElement(it) })
            is PrimitiveNode -> node.value
        }

    }

}